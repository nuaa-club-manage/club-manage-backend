package com.nuaa.club_manage_backend.config;

import com.nuaa.club_manage_backend.config.interceptor.JwtInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web 全局配置类
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    // 将刚才写的拦截器交给 Spring 容器管理
    @Bean
    public JwtInterceptor jwtInterceptor() {
        return new JwtInterceptor();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(jwtInterceptor())
                // 1. 拦截所有的 /api/ 开头的请求
                .addPathPatterns("/api/**")
                // 2. 划定白名单：登录和注册接口放行
                .excludePathPatterns(
                        "/api/user/login",
                        "/api/user/register",
                        "/api/user/captcha",
                        "/api/user/sendCode",
                        "/api/user/sendResetCode",
                        "/api/user/resetPassword",
                        "/api/admin/login"
                );
    }
}