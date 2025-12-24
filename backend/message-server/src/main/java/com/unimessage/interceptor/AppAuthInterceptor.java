package com.unimessage.interceptor;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.unimessage.context.AppContext;
import com.unimessage.entity.SysApp;
import com.unimessage.mapper.SysAppMapper;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * App 鉴权拦截器
 * 从请求头中获取 X-App-Key 和 X-App-Secret 进行验证
 *
 * @author 海明
 */
@Slf4j
@Component
public class AppAuthInterceptor implements HandlerInterceptor {

    @Resource
    private SysAppMapper appMapper;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        String appKey = request.getHeader("X-App-Key");
        String appSecret = request.getHeader("X-App-Secret");

        if (appKey == null || appKey.isEmpty()) {
            response.setStatus(401);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"success\":false,\"message\":\"缺少 X-App-Key 请求头\"}");
            return false;
        }

        if (appSecret == null || appSecret.isEmpty()) {
            response.setStatus(401);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"success\":false,\"message\":\"缺少 X-App-Secret 请求头\"}");
            return false;
        }

        // 查询 App
        LambdaQueryWrapper<SysApp> query = new LambdaQueryWrapper<>();
        query.eq(SysApp::getAppKey, appKey);
        SysApp app = appMapper.selectOne(query);

        if (app == null) {
            response.setStatus(401);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"success\":false,\"message\":\"无效的 App Key\"}");
            return false;
        }

        if (!app.getAppSecret().equals(appSecret)) {
            response.setStatus(401);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"success\":false,\"message\":\"App Secret 错误\"}");
            return false;
        }

        if (app.getStatus() != 1) {
            response.setStatus(403);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"success\":false,\"message\":\"应用已被禁用\"}");
            return false;
        }

        // 验证通过，存入上下文
        AppContext.setCurrentApp(app);
        log.debug("App 鉴权成功: appCode={}, appName={}", app.getAppCode(), app.getAppName());
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
            throws Exception {
        // 清理 ThreadLocal
        AppContext.clear();
    }
}
