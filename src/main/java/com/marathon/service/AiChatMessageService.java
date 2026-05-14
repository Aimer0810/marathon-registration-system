package com.marathon.service;

import com.marathon.entity.AiChatMessage;
import com.marathon.mapper.AiChatMessageMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * AI 聊天记录服务
 *
 * 这个 Service 非常薄——只是在 AiController 处理完 AI 回复后，
 * 把一问一答保存到数据库。查询也简单，按 userId 查即可。
 *
 * 前端 AiAssistant 组件打开时会调用 getUserMessages() 加载历史记录。
 */
@Service
public class AiChatMessageService {
    @Autowired
    private AiChatMessageMapper aiChatMessageMapper;

    /**
     * 保存一条聊天记录
     * @param userId   哪个用户
     * @param eventId  关联的赛事（可为 null）
     * @param question 用户的问题
     * @param answer   AI 的回复
     */
    public void addMessage(Long userId, Long eventId, String question, String answer) {
        AiChatMessage message = new AiChatMessage();
        message.setUserId(userId);
        message.setEventId(eventId);
        message.setQuestion(question);
        message.setAnswer(answer);
        aiChatMessageMapper.insert(message);
    }

    /** 获取某用户的全部聊天历史 */
    public List<AiChatMessage> getUserMessages(Long userId) {
        return aiChatMessageMapper.selectByUserId(userId);
    }
}
