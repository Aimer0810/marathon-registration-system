package com.marathon.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.marathon.entity.Announcement;
import com.marathon.entity.Event;
import com.marathon.entity.Favorite;
import com.marathon.entity.Message;
import com.marathon.entity.Registration;
import com.marathon.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AiService {

    @Value("${ai.deepseek.base-url:https://api.deepseek.com}")
    private String baseUrl;

    @Value("${ai.deepseek.api-key:}")
    private String apiKey;

    @Value("${ai.deepseek.model:deepseek-v4-pro}")
    private String model;

    @Value("${ai.deepseek.temperature:0.7}")
    private double temperature;

    @Autowired
    private EventService eventService;

    @Autowired
    private AnnouncementService announcementService;

    @Autowired
    private RegistrationService registrationService;

    @Autowired
    private FavoriteService favoriteService;

    @Autowired
    private MessageService messageService;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public String chat(String userMessage, User currentUser) {
        if (userMessage == null || userMessage.trim().isEmpty()) {
            return "请输入你想咨询的问题。";
        }
        String trimmedMessage = userMessage.trim();

        if (apiKey == null || apiKey.trim().isEmpty() || "your-deepseek-api-key".equals(apiKey)) {
            return "AI 助手尚未配置 DeepSeek API Key，请先在 application.yml 中填写 ai.deepseek.api-key。";
        }

        String systemPrompt = buildSystemPrompt(currentUser);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", model);
        requestBody.put("temperature", temperature);
        requestBody.put("messages", Arrays.asList(
                buildMessage("system", systemPrompt),
                buildMessage("user", trimmedMessage)
        ));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<byte[]> response = restTemplate.exchange(
                    baseUrl + "/chat/completions",
                    HttpMethod.POST,
                    entity,
                    byte[].class
            );
            String body = new String(response.getBody(), StandardCharsets.UTF_8);
            JsonNode root = objectMapper.readTree(body);
            JsonNode contentNode = root.path("choices").path(0).path("message").path("content");
            if (contentNode.isMissingNode() || contentNode.asText().trim().isEmpty()) {
                return "AI 助手没有返回有效内容，请稍后重试。";
            }
            return contentNode.asText().trim();
        } catch (Exception e) {
            return "调用 DeepSeek 失败：" + e.getMessage();
        }
    }

    private Map<String, String> buildMessage(String role, String content) {
        Map<String, String> message = new HashMap<>();
        message.put("role", role);
        message.put("content", content);
        return message;
    }

    private String buildSystemPrompt(User currentUser) {
        List<Event> events = eventService.getAllEvents();
        List<Announcement> announcements = announcementService.getAll();

        String eventText = events.stream()
                .limit(12)
                .map(e -> String.format(
                        "赛事：%s；地点：%s；日期：%s；状态：%s；费用：%s",
                        safe(e.getName()),
                        safe(e.getLocation()),
                        safe(String.valueOf(e.getEventDate())),
                        eventStatusText(e.getStatus()),
                        e.getEntryFee() == null ? "0" : e.getEntryFee().toPlainString()
                ))
                .collect(Collectors.joining("\n"));

        String announcementText = announcements.stream()
                .limit(8)
                .map(a -> String.format(
                        "公告：%s；发布时间：%s",
                        safe(a.getTitle()),
                        safe(String.valueOf(a.getPublishTime()))
                ))
                .collect(Collectors.joining("\n"));

        StringBuilder prompt = new StringBuilder();
        prompt.append("你是马拉松报名系统的 AI 助手。\n");
        prompt.append("请始终使用简体中文回答。\n");
        prompt.append("你对外保持“马拉松报名系统 AI 助手”的产品身份。\n");
        prompt.append("如果用户询问模型来源、供应商、底层模型或你是什么大模型，必须如实回答：当前由 DeepSeek 提供支持，当前接入模型是 ")
                .append(model)
                .append("。\n");
        prompt.append("其他问题可以自由回答，不再只限制在赛事、公告、报名、收藏、留言等系统话题。\n");
        prompt.append("如果问题与本系统相关，优先结合系统中的真实数据回答。\n");
        prompt.append("如果问题与本系统无关，也可以正常回答，但不要伪造系统内不存在的数据。\n");
        prompt.append("不要泄露其他用户的隐私数据、账号信息、报名详情或后台敏感信息。\n");
        prompt.append("如果当前用户未登录，不要回答其个人报名、收藏、留言等个人数据。\n");
        prompt.append("回答尽量准确、简洁、直接。\n\n");

        prompt.append("系统赛事数据：\n").append(eventText).append("\n\n");
        prompt.append("系统公告数据：\n").append(announcementText).append("\n\n");

        if (currentUser != null) {
            List<Registration> registrations = registrationService.getUserRegistrations(currentUser.getId());
            List<Favorite> favorites = favoriteService.getUserFavorites(currentUser.getId());
            List<Message> messages = messageService.getUserMessages(currentUser.getId());

            prompt.append("当前登录用户：")
                    .append(safe(currentUser.getUsername()))
                    .append("；真实姓名：")
                    .append(safe(currentUser.getRealName()))
                    .append("\n");
            prompt.append("我的报名数量：").append(registrations.size())
                    .append("；我的收藏数量：").append(favorites.size())
                    .append("；我的留言数量：").append(messages.size())
                    .append("\n");

            if (!registrations.isEmpty()) {
                prompt.append("我的报名记录：\n");
                prompt.append(registrations.stream()
                        .limit(6)
                        .map(r -> String.format("%s（状态：%s）", safe(r.getEventName()), registrationStatusText(r.getStatus())))
                        .collect(Collectors.joining("\n")));
                prompt.append("\n");
            }
        } else {
            prompt.append("当前用户未登录。\n");
        }

        return prompt.toString();
    }

    private String safe(String value) {
        return value == null ? "无" : value;
    }

    private String eventStatusText(Integer status) {
        if (status == null) {
            return "未知";
        }
        switch (status) {
            case 0:
                return "未开始";
            case 1:
                return "报名中";
            case 2:
                return "已截止";
            case 3:
                return "已结束";
            default:
                return "未知";
        }
    }

    private String registrationStatusText(Integer status) {
        if (status == null) {
            return "未知";
        }
        switch (status) {
            case 0:
                return "待审核";
            case 1:
                return "已通过";
            case 2:
                return "已拒绝";
            case 3:
                return "已取消";
            default:
                return "未知";
        }
    }
}
