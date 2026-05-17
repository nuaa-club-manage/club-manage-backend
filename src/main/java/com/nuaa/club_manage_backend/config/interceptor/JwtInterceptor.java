package com.nuaa.club_manage_backend.config.interceptor;

import com.nuaa.club_manage_backend.exception.BusinessException;
import com.nuaa.club_manage_backend.utils.JwtUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * JWT Token 拦截器
 */
public class JwtInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // 1. 放行浏览器出于跨域安全机制发出的 OPTIONS 预检请求
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        // 2. 从 HTTP 请求头中拿到前端约定好的 "Authorization" 字段（也就是 Token）
        String token = request.getHeader("Authorization");

        // 3. 如果连 Token 都没有，直接抛出 401 异常，全局异常处理器会自动接管
        if (token == null || token.trim().isEmpty()) {
            throw new BusinessException(401, "无访问权限，请先登录");
        }

        // 4. 去除 "Bearer " 前缀（前端标准 Authorization 头格式）
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        // 5. 有 Token，找我们之前写的 JwtUtils 工具类鉴定真伪
        String userId = JwtUtils.getUserIdByToken(token);
        if (userId == null) {
            // 解析失败（被篡改或已过期 7 天）
            throw new BusinessException(401, "登录身份已过期，请重新登录");
        }

        // 5. 鉴定为真！甚至我们可以贴心地把 userId 挂在 request 上，
        // 这样后面的 Controller 就不需要再解析一遍了，直接拿来用。
        request.setAttribute("currentUserId", userId);

        // 6. 放行，允许进入后面的 Controller
        return true;
    }
}