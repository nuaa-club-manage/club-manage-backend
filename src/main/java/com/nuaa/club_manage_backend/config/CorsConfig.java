package com.nuaa.club_manage_backend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 全局跨域配置
 */
@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // 设置允许跨域的路径
        registry.addMapping("/**")
                // 设置允许跨域请求的域名 (Spring Boot 2.4+ 建议使用 allowedOriginPatterns)
                .allowedOriginPatterns("*")
                // 是否允许证书 (Cookies)
                .allowCredentials(true)
                // 设置允许的方法
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                // 设置允许的 header
                .allowedHeaders("*")
                // 跨域允许时间
                .maxAge(3600);
    }
}