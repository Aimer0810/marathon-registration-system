package com.marathon.mapper;

import com.marathon.entity.AiChatMessage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

/**
 * AI 聊天记录数据访问接口
 *
 * 每次用户和 AI 助手对话都会存一条记录（一问一答），
 * 用于加载历史聊天记录。
 */
@Mapper
public interface AiChatMessageMapper {
    int insert(AiChatMessage message);                // 保存一条聊天记录
    List<AiChatMessage> selectByUserId(@Param("userId") Long userId); // 查某用户的所有聊天历史
}
