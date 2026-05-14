package com.marathon.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * AI 聊天记录实体类 —— 对应数据库中的 ai_chat_message 表
 *
 * 每次用户跟 AI 助手聊天，一问一答都会被保存到这张表。
 * 这样用户下次打开 AI 助手面板时，就可以加载历史聊天记录了。
 *
 * eventId 是可选的：如果用户在某个赛事详情页里问问题，会关联那个赛事；
 * 如果只是随便聊天，eventId 就是 null。
 */
@Data
public class AiChatMessage {
    private Long id;               // 聊天记录编号
    private Long userId;           // 哪个用户聊的
    private String question;       // 用户问的问题
    private String answer;         // AI 给出的回答
    private Long eventId;          // 关联的赛事 ID（可为空，表示没有关联特定赛事）
    private LocalDateTime createTime;  // 聊天时间
}
