package com.marathon.dto;

import java.time.LocalDateTime;

public class AiChatHistoryItem {
    private String role;
    private String content;
    private LocalDateTime createTime;

    public AiChatHistoryItem() {
    }

    public AiChatHistoryItem(String role, String content, LocalDateTime createTime) {
        this.role = role;
        this.content = content;
        this.createTime = createTime;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }
}
