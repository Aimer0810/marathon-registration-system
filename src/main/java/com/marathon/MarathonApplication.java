package com.marathon;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 马拉松报名系统 —— 启动入口
 *
 * 什么是 @SpringBootApplication？
 *   这是 Spring Boot 最核心的注解，它是一个"组合注解"，相当于同时加了：
 *     @Configuration  — 标记这是一个配置类
 *     @EnableAutoConfiguration — 自动配置（Spring Boot 的核心能力：根据依赖自动设置）
 *     @ComponentScan  — 自动扫描当前包及子包下的所有组件（@Service、@Controller 等）
 *
 * main 方法：
 *   SpringApplication.run() 会启动内嵌的 Tomcat 服务器，默认监听 8080 端口，
 *   然后扫描并初始化所有的 Bean（Controller、Service、Mapper 等），
 *   最后等待 HTTP 请求到来。
 *
 *   你不需要手动部署到 Tomcat——Spring Boot 自带了一个内嵌的 Tomcat。
 */
@SpringBootApplication
public class MarathonApplication {
    public static void main(String[] args) {
        SpringApplication.run(MarathonApplication.class, args);
    }
}
