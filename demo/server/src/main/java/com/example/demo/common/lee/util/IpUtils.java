package com.example.demo.common.lee.util;

import javax.servlet.http.HttpServletRequest;

/**
 * 工具类，用于获取客户端的IP地址。
 */
public final class IpUtils {

    // 私有构造函数，防止实例化
    private IpUtils() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    /**
     * 获取用户发送请求的IP地址。
     *
     * @param request {@link HttpServletRequest} 对象
     * @return 用户的IP地址，如果无法获取则返回空字符串
     */
    public static String getClientIp(HttpServletRequest request) {
        if (request == null) {
            return "";
        }

        // 获取直接连接的远程地址
        String remoteAddr = request.getRemoteAddr();
        return remoteAddr != null ? remoteAddr : "";
    }
}
