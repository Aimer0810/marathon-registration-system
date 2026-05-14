package com.marathon.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 留言实体类 —— 对应数据库中的 message 表
 *
 * 普通用户可以在系统里提交留言/问题，管理员可以回复。
 * 比如用户留言："报名成功后能不能修改尺码？"管理员回复："报名截止前可以联系工作人员修改。"
 */
@Data
public class Message {
    private Long id;               // 留言编号
    private Long userId;           // 留言人用户 ID
    private String content;        // 留言内容（用户写的）
    private String reply;          // 管理员回复内容（为空表示还没回复）
    private LocalDateTime createTime;   // 留言时间

    // 扩展字段：不在 message 表中，来自 user 表的 JOIN 查询
    private String userName;       // 留言人的用户名，管理员查看时方便知道是谁留的
}
