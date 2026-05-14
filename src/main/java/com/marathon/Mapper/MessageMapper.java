package com.marathon.mapper;

import com.marathon.entity.Message;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

/**
 * 留言数据访问接口
 *
 * updateReply：管理员回复留言时调用，把 reply 字段从空更新为回复内容
 */
@Mapper
public interface MessageMapper {
    int insert(Message message);                                      // 用户提交留言
    int updateReply(@Param("id") Long id, @Param("reply") String reply); // 管理员回复
    List<Message> selectByUserId(Long userId);                        // 查某个用户的留言
    List<Message> selectAll();                                        // 查所有留言（管理员看）
}
