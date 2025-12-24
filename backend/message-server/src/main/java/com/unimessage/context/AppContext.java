package com.unimessage.context;

import com.unimessage.entity.SysApp;

/**
 * App 上下文
 * 用于在请求线程中传递当前调用方信息
 *
 * @author 海明
 * @since 2025-12-04
 */
public class AppContext {

    private static final ThreadLocal<SysApp> CURRENT_APP = new ThreadLocal<>();

    /**
     * 获取当前请求的 App 信息
     *
     * @return 应用信息
     */
    public static SysApp getCurrentApp() {
        return CURRENT_APP.get();
    }

    /**
     * 设置当前请求的 App 信息
     *
     * @param app 应用信息
     */
    public static void setCurrentApp(SysApp app) {
        CURRENT_APP.set(app);
    }

    /**
     * 获取当前请求的 App ID
     *
     * @return 应用 ID
     */
    public static Long getCurrentAppId() {
        SysApp app = CURRENT_APP.get();
        return app != null ? app.getId() : null;
    }

    /**
     * 清理 ThreadLocal，防止内存泄漏
     */
    public static void clear() {
        CURRENT_APP.remove();
    }
}
