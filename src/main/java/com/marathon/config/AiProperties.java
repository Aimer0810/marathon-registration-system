package com.marathon.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * AI 配置类 —— 绑定 application.yml 中 ai.deepseek 开头的配置项
 *
 * 什么是 @ConfigurationProperties？
 *   Spring Boot 提供的注解，会把配置文件中的值自动注入到这个类的字段里。
 *   比如 application.yml 里写了 ai.deepseek.model: deepseek-chat
 *   Spring 就会自动调用 setModel("deepseek-chat")。
 *
 *   前缀 prefix = "ai.deepseek" 表示只绑定以 ai.deepseek 开头的配置项。
 *   例如：ai.deepseek.enabled → setEnabled()
 *        ai.deepseek.api-key → setApiKey()
 *
 * 什么是 @Component？
 *   告诉 Spring："这个类请帮我创建并管理一个实例"。
 *   这样其他地方（比如 AiService）就可以通过 @Autowired 或构造函数注入来使用它。
 */
@Component
@ConfigurationProperties(prefix = "ai.deepseek")
public class AiProperties {
    /** AI 功能总开关，false 时所有 AI 功能不可用 */
    private boolean enabled = true;

    /** DeepSeek API 的接口地址，如 https://api.deepseek.com */
    private String baseUrl;

    /** DeepSeek API 的密钥（sk- 开头），用于身份验证。注意：生产环境应该用环境变量，不要写死在配置文件里 */
    private String apiKey;

    /** 使用的模型名称 **/
    private String model = "deepseek-v4-pro";

    /**
     * 模型温度参数（0~2），控制回答的随机性：
     *   0 = 最确定，每次回答几乎一样（适合编程/数学）
     *   1 = 平衡
     *   越高越有"创意"但越不稳定
     */
    private Double temperature = 0.7;

    // ===== Getter / Setter（Spring 通过反射调用它们来赋值） =====
    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }
    public String getBaseUrl() { return baseUrl; }
    public void setBaseUrl(String baseUrl) { this.baseUrl = baseUrl; }
    public String getApiKey() { return apiKey; }
    public void setApiKey(String apiKey) { this.apiKey = apiKey; }
    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }
    public Double getTemperature() { return temperature; }
    public void setTemperature(Double temperature) { this.temperature = temperature; }
}
