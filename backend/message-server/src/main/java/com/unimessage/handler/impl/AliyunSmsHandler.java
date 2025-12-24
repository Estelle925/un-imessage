package com.unimessage.handler.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.unimessage.entity.LogMsgDetail;
import com.unimessage.entity.SysChannel;
import com.unimessage.entity.SysTemplate;
import com.unimessage.enums.ChannelType;
import com.unimessage.handler.ChannelHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 阿里云短信发送处理器
 *
 * @author 海明
 * @since 2025-12-04
 */
@Slf4j
@Component
public class AliyunSmsHandler implements ChannelHandler {

    private static final String DEFAULT_ENDPOINT = "dysmsapi.aliyuncs.com";
    private static final String SUCCESS_CODE = "OK";
    private static final String ERROR_CODE_TEST_LIMIT = "isv.SMS_TEST_SIGN_TEMPLATE_LIMIT";
    private static final String JSON_KEY_ACCESS_KEY_ID = "accessKeyId";
    private static final String JSON_KEY_ACCESS_KEY_SECRET = "accessKeySecret";
    private static final String JSON_KEY_SIGN_NAME = "signName";
    private static final String JSON_KEY_ENDPOINT = "endpoint";
    private static final String EMPTY_JSON = "{}";

    @Override
    public boolean support(String channelType) {
        return ChannelType.SMS.getCode().equals(channelType);
    }

    @Override
    public boolean send(SysChannel channel, SysTemplate template, LogMsgDetail msgDetail, Map<String, Object> params) {
        log.info("开始发送阿里云短信: recipient={}", msgDetail.getRecipient());

        try {
            JSONObject config = JSON.parseObject(channel.getConfigJson());
            String accessKeyId = config.getString(JSON_KEY_ACCESS_KEY_ID);
            String accessKeySecret = config.getString(JSON_KEY_ACCESS_KEY_SECRET);
            String signName = config.getString(JSON_KEY_SIGN_NAME);

            validateConfig(accessKeyId, accessKeySecret, signName);

            String templateCode = template.getThirdPartyId();
            log.info("准备发送阿里云短信: recipient={}, signName={}, templateCode={}",
                    msgDetail.getRecipient(), signName, templateCode);

            String endpoint = config.getString(JSON_KEY_ENDPOINT);
            if (endpoint == null || endpoint.isEmpty()) {
                endpoint = DEFAULT_ENDPOINT;
            }

            com.aliyun.teaopenapi.models.Config clientConfig = new com.aliyun.teaopenapi.models.Config()
                    .setAccessKeyId(accessKeyId)
                    .setAccessKeySecret(accessKeySecret)
                    .setEndpoint(endpoint);

            com.aliyun.dysmsapi20170525.Client client = new com.aliyun.dysmsapi20170525.Client(clientConfig);

            // 阿里云短信要求参数值为字符串，且验证码类型短信对字符集有严格要求（通常为[a-zA-Z0-9]）
            // 这里将所有参数转换为String类型，避免数字类型导致的格式问题
            String paramJson = EMPTY_JSON;
            if (params != null && !params.isEmpty()) {
                java.util.Map<String, String> stringParams = new java.util.HashMap<>();
                for (Map.Entry<String, Object> entry : params.entrySet()) {
                    if (entry.getValue() != null) {
                        stringParams.put(entry.getKey(), String.valueOf(entry.getValue()));
                    }
                }
                paramJson = JSON.toJSONString(stringParams);
            }

            // 保存发送内容快照
            msgDetail.setContent("SignName: " + signName + ", TemplateCode: " + template.getThirdPartyId() + ", Params: " + paramJson);

            com.aliyun.dysmsapi20170525.models.SendSmsRequest sendSmsRequest = new com.aliyun.dysmsapi20170525.models.SendSmsRequest()
                    .setPhoneNumbers(msgDetail.getRecipient())
                    .setSignName(signName)
                    .setTemplateCode(template.getThirdPartyId())
                    .setTemplateParam(paramJson);

            com.aliyun.dysmsapi20170525.models.SendSmsResponse response = client.sendSms(sendSmsRequest);

            String code = response.getBody().getCode();
            String message = response.getBody().getMessage();

            if (!SUCCESS_CODE.equals(code)) {
                log.error("阿里云短信发送失败: code={}, message={}", code, message);

                if (ERROR_CODE_TEST_LIMIT.equals(code)) {
                    log.error("【解决方案】您正在使用阿里云测试签名或模板，请确保：1. 签名必须是'阿里云短信测试' 2. 模板必须是测试专用模板(如SMS_154950909) 3. 接收手机号必须已绑定测试名单");
                }

                msgDetail.setErrorMsg(message);
                return false;
            }

            msgDetail.setThirdPartyMsgId(response.getBody().getBizId());
            log.info("阿里云短信发送成功: bizId={}", response.getBody().getBizId());
            return true;

        } catch (Exception e) {
            log.error("阿里云短信发送异常", e);
            msgDetail.setErrorMsg(e.getMessage());
            return false;
        }
    }

    /**
     * 校验配置参数
     */
    private void validateConfig(String accessKeyId, String accessKeySecret, String signName) {
        if (accessKeyId == null || accessKeyId.isEmpty()) {
            throw new IllegalArgumentException("阿里云短信配置缺失: accessKeyId 为空");
        }
        if (accessKeySecret == null || accessKeySecret.isEmpty()) {
            throw new IllegalArgumentException("阿里云短信配置缺失: accessKeySecret 为空");
        }
        if (signName == null || signName.isEmpty()) {
            throw new IllegalArgumentException("阿里云短信配置缺失: signName 为空");
        }
    }
}
