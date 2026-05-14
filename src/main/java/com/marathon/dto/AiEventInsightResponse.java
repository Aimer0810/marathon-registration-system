package com.marathon.dto;

/**
 * AI 赛事解读响应 —— AI 对某一场赛事写的分析报告
 *
 * 用户点击赛事详情页的"生成解读"按钮，后端会调用 DeepSeek，
 * 让它针对这场赛事输出三段话：适合什么人、关注什么、怎么准备。
 * 返回结果就是 summary 字段的内容。
 */
public class AiEventInsightResponse {
    private Long eventId;     // 被解读的赛事 ID
    private String summary;   // AI 生成的赛事解读全文（220字以内）

    public AiEventInsightResponse() {}

    public AiEventInsightResponse(Long eventId, String summary) {
        this.eventId = eventId;
        this.summary = summary;
    }

    public Long getEventId() { return eventId; }
    public void setEventId(Long eventId) { this.eventId = eventId; }
    public String getSummary() { return summary; }
    public void setSummary(String summary) { this.summary = summary; }
}
