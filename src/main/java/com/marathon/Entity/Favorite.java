package com.marathon.entity;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 收藏实体类 —— 对应数据库中的 favorite 表
 *
 * 用户可以收藏自己感兴趣的赛事，方便以后查看。
 * 一个用户可以收藏多个赛事，一个赛事也可以被多个用户收藏（多对多关系）。
 *
 * 注意：eventName、eventLocation、eventDate 这三个字段是冗余存储的。
 * 意思是虽然 event 表里已经有这些信息了，但为了方便直接展示收藏列表
 * 而不需要每次都 JOIN event 表，所以在 favorite 里也存了一份。
 */
@Data
public class Favorite {
    private Long id;               // 收藏记录编号
    private Long userId;           // 哪个用户收藏的
    private Long eventId;          // 收藏的赛事 ID

    // 以下三个字段是冗余存储（也存在于 event 表），用于收藏列表直接展示
    private String eventName;      // 赛事名称
    private String eventLocation;  // 赛事地点
    private LocalDate eventDate;   // 比赛日期

    private LocalDateTime createTime;  // 收藏时间
}
