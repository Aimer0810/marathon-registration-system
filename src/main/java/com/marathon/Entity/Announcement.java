package com.marathon.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 公告实体类 —— 对应数据库中的 announcement 表
 *
 * 管理员发布的系统公告，所有用户都能看到。
 * 比如："2026上海马拉松报名延迟通知"、"系统维护公告" 等。
 */
@Data
public class Announcement {
    private Long id;               // 公告编号
    private String title;          // 公告标题
    private String content;        // 公告正文内容
    private LocalDateTime publishTime;   // 发布时间
    private Long publisherId;      // 发布人的用户 ID（对应 user 表）

    // 扩展字段：不在 announcement 表中，是 SQL JOIN 查询时带出来的发布人姓名
    private String publisherName;
}
