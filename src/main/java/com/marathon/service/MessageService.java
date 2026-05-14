package com.marathon.service;

import com.marathon.entity.Message;
import com.marathon.mapper.MessageMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 留言业务服务
 *
 * 流程：
 *   用户提交留言 → status 默认为"未回复"（reply 字段为空）
 *   管理员查看留言列表 → 对某条留言进行回复 → updateReply()
 *   用户在"我的留言"页面可以看到自己的留言和管理员的回复
 */
@Service
public class MessageService {
    @Autowired
    private MessageMapper messageMapper;

    /** 用户提交留言 */
    public void add(Message message) {
        messageMapper.insert(message);
    }

    /** 管理员回复留言 */
    public void reply(Long id, String reply) {
        messageMapper.updateReply(id, reply);
    }

    /** 查询某个用户的所有留言（用于"我的留言"页面） */
    public List<Message> getUserMessages(Long userId) {
        return messageMapper.selectByUserId(userId);
    }

    /** 查询所有留言（管理员查看用，含所有用户的留言） */
    public List<Message> getAllMessages() {
        return messageMapper.selectAll();
    }
}
