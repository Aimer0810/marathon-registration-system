package com.marathon.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 跨域配置 —— 允许前端（Vue 开发服务器）访问后端 API
 *
 * 什么是跨域（CORS）？
 *   浏览器的安全策略：默认情况下，一个域名下的网页不能请求另一个域名的数据。
 *   前端开发服务器跑在 localhost:5173，后端跑在 localhost:8080，
 *   端口不同就是"跨域"，浏览器会阻止请求。
 *
 * 这个配置就是告诉 Spring："来自 localhost:5173 的请求我是认可的，放行"。
 *
 * allowCredentials(true) 表示允许前端携带 Cookie/Authorization 头。
 * 注意：设置了 allowCredentials 后，allowedOrigins 就不能用 "*"。
 *
 * 实际上生产部署时，前后端会放在同一个域名下，就不需要这个配置了。
 */
@Configuration  // 告诉 Spring 这是一个配置类（启动时会自动加载）
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")              // 对所有接口路径生效
                .allowedOrigins(                 // 允许这些来源的跨域请求：
                        "http://localhost:5173",   // Vite 默认端口
                        "http://localhost:5174",   // Vite 备用端口
                        "http://127.0.0.1:5173",   // 127.0.0.1 和 localhost 在浏览器眼里是不同的"源"
                        "http://127.0.0.1:5174"
                )
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")  // 允许的 HTTP 方法
                .allowedHeaders("*")               // 允许所有请求头
                .allowCredentials(true)            // 允许携带身份凭证（token）
                .maxAge(3600);                     // 预检请求缓存 1 小时（避免每次请求都先发 OPTIONS）
    }
}
