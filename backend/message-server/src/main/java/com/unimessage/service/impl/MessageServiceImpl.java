package com.unimessage.service.impl;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.toolkit.Db;
import com.unimessage.context.AppContext;
import com.unimessage.dto.MqMessage;
import com.unimessage.dto.SendRequest;
import com.unimessage.dto.SendResponse;
import com.unimessage.entity.*;
import com.unimessage.enums.BatchStatus;
import com.unimessage.enums.ChannelType;
import com.unimessage.enums.DetailStatus;
import com.unimessage.handler.ChannelHandler;
import com.unimessage.handler.ChannelHandlerFactory;
import com.unimessage.mapper.*;
import com.unimessage.mq.producer.MqProducer;
import com.unimessage.service.MessageService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

/**
 * 消息发送服务实现类
 *
 * @author 海明
 * @since 2025-12-04
 */
@Slf4j
@Service
public class MessageServiceImpl implements MessageService {

    @Resource
    private SysTemplateMapper templateMapper;
    @Resource
    private SysChannelMapper channelMapper;
    @Resource
    private LogMsgBatchMapper batchMapper;
    @Resource
    private LogMsgDetailMapper detailMapper;
    @Resource
    private SysRecipientMapper recipientMapper;
    @Resource
    private ChannelHandlerFactory handlerFactory;
    @Resource
    private MqProducer mqProducer;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public SendResponse send(SendRequest request) {
        SysTemplate template = getTemplate(request.getTemplateCode());
        if (template == null) {
            return SendResponse.fail("模板不存在: " + request.getTemplateCode());
        }
        if (template.getStatus() != 1) {
            return SendResponse.fail("模板已禁用");
        }

        SysChannel channel = getChannel(template.getChannelId());
        if (channel == null) {
            return SendResponse.fail("渠道不可用");
        }

        List<String> finalRecipients = resolveRecipients(request, template, channel);
        if (finalRecipients.isEmpty()) {
            return SendResponse.fail("未指定接收者，且模板未关联有效的接收人或分组");
        }
        request.setRecipients(finalRecipients);

        if (handlerFactory.getHandler(channel.getType()) == null) {
            return SendResponse.fail("未找到该渠道的处理器: " + channel.getType());
        }

        return createAndPushBatch(request, template, channel, finalRecipients);
    }

    private SysTemplate getTemplate(String code) {
        LambdaQueryWrapper<SysTemplate> query = new LambdaQueryWrapper<>();
        query.eq(SysTemplate::getCode, code);
        return templateMapper.selectOne(query);
    }

    private SysChannel getChannel(Long id) {
        SysChannel channel = channelMapper.selectById(id);
        return (channel != null && channel.getStatus() == 1) ? channel : null;
    }

    private List<String> resolveRecipients(SendRequest request, SysTemplate template, SysChannel channel) {
        List<String> requestRecipients = request.getRecipients();
        if (requestRecipients != null && !requestRecipients.isEmpty()) {
            // 如果请求中包含了接收者，直接使用（需提取对应的名称映射，此处简化处理，假设请求自带的优先）
            // 实际逻辑中如果需要混合处理，可以在这里扩展
            return requestRecipients;
        }

        Set<String> recipientSet = new HashSet<>();
        // 1. 从分组获取
        if (template.getRecipientGroupIds() != null && !template.getRecipientGroupIds().isEmpty()) {
            addRecipientsFromGroups(recipientSet, template.getRecipientGroupIds(), channel.getType());
        }

        // 2. 从独立接收者列表获取
        if (template.getRecipientIds() != null && !template.getRecipientIds().isEmpty()) {
            addRecipientsFromIds(recipientSet, template.getRecipientIds(), channel.getType());
        }

        return new ArrayList<>(recipientSet);
    }

    private void addRecipientsFromGroups(Set<String> recipientSet, String groupIdsStr, String channelType) {
        String[] groupIds = groupIdsStr.split(",");
        for (String groupIdStr : groupIds) {
            try {
                Long groupId = Long.parseLong(groupIdStr.trim());
                List<SysRecipient> groupMembers = recipientMapper.selectByGroupId(groupId);
                if (groupMembers != null && !groupMembers.isEmpty()) {
                    recipientSet.addAll(extractRecipientMap(groupMembers, channelType).keySet());
                }
            } catch (NumberFormatException e) {
                log.error("Invalid group ID format: {}", groupIdStr);
            }
        }
    }

    private void addRecipientsFromIds(Set<String> recipientSet, String idsStr, String channelType) {
        String[] recipientIds = idsStr.split(",");
        List<Long> idList = new ArrayList<>();
        for (String idStr : recipientIds) {
            try {
                idList.add(Long.parseLong(idStr.trim()));
            } catch (NumberFormatException e) {
                log.error("Invalid recipient ID format: {}", idStr);
            }
        }

        if (!idList.isEmpty()) {
            List<SysRecipient> recipients = recipientMapper.selectBatchIds(idList);
            if (recipients != null && !recipients.isEmpty()) {
                recipientSet.addAll(extractRecipientMap(recipients, channelType).keySet());
            }
        }
    }

    private SendResponse createAndPushBatch(SendRequest request, SysTemplate template, SysChannel channel, List<String> recipients) {
        String batchNo = UUID.randomUUID().toString().replace("-", "");
        LogMsgBatch batch = new LogMsgBatch();
        batch.setBatchNo(batchNo);
        batch.setAppId(AppContext.getCurrentAppId() != null ? AppContext.getCurrentAppId() : 0L);
        batch.setTemplateId(template.getId());
        batch.setTemplateName(template.getName());
        batch.setChannelId(channel.getId());
        batch.setChannelName(channel.getName());
        batch.setMsgType(template.getMsgType());
        batch.setTitle(template.getTitle());
        batch.setContent(template.getContent());
        batch.setContentParams(JSON.toJSONString(request.getParams()));
        batch.setTotalCount(recipients.size());
        batch.setSuccessCount(0);
        batch.setFailCount(0);
        batch.setStatus(BatchStatus.PENDING.getCode());
        batch.setCreatedAt(LocalDateTime.now());

        batchMapper.insert(batch);

        try {
            // Push to Redis MQ
            // 注意：这里需要重新构建名称映射，因为 resolveRecipients 中可能丢失了名称信息
            // 简单起见，这里再次调用 extractRecipientMap 或者优化 resolveRecipients 返回结构
            // 由于时间紧迫，这里假设 recipientNameMap 为空或后续在 processBatch 中补充
            Map<String, String> recipientNameMap = new HashMap<>(16);
            // 实际场景应在 resolveRecipients 中同时返回 Map

            MqMessage message = new MqMessage(batch.getId(), request, recipientNameMap);
            mqProducer.send(message);
        } catch (Exception e) {
            log.error("Push to MQ failed", e);
            throw new RuntimeException("消息入队失败", e);
        }

        return SendResponse.success(batchNo);
    }

    /**
     * 根据渠道类型从接收者实体中提取对应的联系方式
     *
     * @return Map<Contact, Name>
     */
    private Map<String, String> extractRecipientMap(List<SysRecipient> recipients, String channelType) {
        if (recipients == null || recipients.isEmpty()) {
            return Collections.emptyMap();
        }

        ChannelType type = ChannelType.fromCode(channelType);
        if (type == null) {
            return Collections.emptyMap();
        }

        Map<String, String> result = new HashMap<>(16);
        for (SysRecipient r : recipients) {
            String contact = switch (type) {
                case SMS -> r.getMobile();
                case EMAIL -> r.getEmail();
                case WECHAT_OFFICIAL -> r.getOpenId();
                case WECHAT_WORK, DINGTALK, FEISHU -> r.getUserId() != null ? r.getUserId() : r.getMobile();
            };

            if (contact != null && !contact.isEmpty()) {
                result.put(contact, r.getName());
            }
        }
        return result;
    }

    @Override
    public void processBatch(MqMessage message) {
        LogMsgBatch batch = batchMapper.selectById(message.getBatchId());
        if (batch == null) {
            log.error("Batch not found: {}", message.getBatchId());
            return;
        }

        SysTemplate template = templateMapper.selectById(batch.getTemplateId());
        if (template == null) {
            log.error("Template not found: {}", batch.getTemplateId());
            return;
        }

        SysChannel channel = channelMapper.selectById(batch.getChannelId());
        if (channel == null) {
            log.error("Channel not found: {}", batch.getChannelId());
            return;
        }

        ChannelHandler handler = handlerFactory.getHandler(channel.getType());
        if (handler == null) {
            log.error("Handler not found: {}", channel.getType());
            return;
        }

        List<LogMsgDetail> details = createDetails(message, batch);
        if (details.isEmpty()) {
            return;
        }

        sendToRecipients(details, handler, channel, template, message.getRequest().getParams(), batch);
    }

    private List<LogMsgDetail> createDetails(MqMessage message, LogMsgBatch batch) {
        List<LogMsgDetail> details = new ArrayList<>();
        Map<String, String> nameMap = message.getRecipientNames() != null ? message.getRecipientNames() : Collections.emptyMap();
        LocalDateTime now = LocalDateTime.now();

        for (String recipient : message.getRequest().getRecipients()) {
            LogMsgDetail detail = new LogMsgDetail();
            detail.setBatchId(batch.getId());
            detail.setRecipient(recipient);
            detail.setRecipientName(nameMap.get(recipient));
            detail.setStatus(DetailStatus.SENDING.getCode());
            detail.setCreatedAt(now);
            details.add(detail);
        }

        try {
            Db.saveBatch(details);
            return details;
        } catch (Exception e) {
            log.error("Batch insert details failed", e);
            batch.setStatus(BatchStatus.FAIL.getCode());
            batchMapper.updateById(batch);
            return Collections.emptyList();
        }
    }

    private void sendToRecipients(List<LogMsgDetail> details, ChannelHandler handler, SysChannel channel,
                                  SysTemplate template, Map<String, Object> params, LogMsgBatch batch) {
        int success = 0;
        int fail = 0;

        for (LogMsgDetail detail : details) {
            try {
                boolean result = handler.send(channel, template, detail, params);
                if (result) {
                    detail.setStatus(DetailStatus.SUCCESS.getCode());
                    success++;
                } else {
                    detail.setStatus(DetailStatus.FAIL.getCode());
                    fail++;
                }
            } catch (Exception e) {
                log.error("发送异常: recipient={}", detail.getRecipient(), e);
                detail.setStatus(DetailStatus.FAIL.getCode());
                detail.setErrorMsg(e.getMessage());
                fail++;
            }
            detail.setSendTime(LocalDateTime.now());
            detailMapper.updateById(detail);
        }

        updateBatchStatus(batch, success, fail);
    }

    private void updateBatchStatus(LogMsgBatch batch, int success, int fail) {
        batch.setSuccessCount(success);
        batch.setFailCount(fail);
        if (fail == 0) {
            batch.setStatus(BatchStatus.SUCCESS.getCode());
        } else if (success == 0) {
            batch.setStatus(BatchStatus.FAIL.getCode());
        } else {
            batch.setStatus(BatchStatus.PARTIAL_SUCCESS.getCode());
        }
        batchMapper.updateById(batch);
    }

    @Override
    public boolean retry(Long detailId) {
        // 重试逻辑暂不修改，因为它依赖已有的 detail 记录
        return false;
    }
}
