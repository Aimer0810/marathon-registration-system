package com.marathon.dto;

/**
 * AI 赛事推荐项 —— AI 推荐功能的单条结果
 *
 * AI 推荐接口会返回一个列表，每个元素就是一个 AiRecommendationItem，
 * 包含推荐的赛事基本信息和推荐理由。
 *
 * 推荐逻辑其实没调用 AI，而是后端代码按"报名中优先 + 日期近的优先"排序取前3条。
 */
public class AiRecommendationItem {
    private Long eventId;       // 赛事 ID，前端可以用来跳转到详情页
    private String eventName;   // 赛事名称
    private String location;    // 比赛地点
    private String eventDate;   // 比赛日期
    private Integer status;     // 赛事状态码（0/1/2/3）
    private String statusText;  // 状态的中文描述（"未开始"/"报名中"/"已截止"/"已结束"）
    private String reason;      // 推荐理由，如"当前处于报名中，适合优先关注"

    // ===== Getter / Setter =====
    public Long getEventId() { return eventId; }
    public void setEventId(Long eventId) { this.eventId = eventId; }
    public String getEventName() { return eventName; }
    public void setEventName(String eventName) { this.eventName = eventName; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public String getEventDate() { return eventDate; }
    public void setEventDate(String eventDate) { this.eventDate = eventDate; }
    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
    public String getStatusText() { return statusText; }
    public void setStatusText(String statusText) { this.statusText = statusText; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
}
