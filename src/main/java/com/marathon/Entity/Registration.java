package com.marathon.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 报名记录实体类 —— 对应数据库中的 registration 表
 *
 * 每次用户报名一个赛事，就会生成一条 Registration 记录。
 * 一条记录 = 哪个用户(userId) 报了哪个赛事(eventId) + 报名信息 + 审核状态。
 *
 * 报名状态流转：
 *   用户提交报名 → 状态=0（待审核）
 *   管理员审核通过 → 状态=1（已通过）
 *   管理员审核拒绝 → 状态=2（已拒绝）
 *   用户自己取消 → 状态=3（已取消）
 */
@Data
public class Registration {
    private Long id;              // 报名记录的唯一编号
    private Long userId;          // 哪个用户报的名（对应 user 表的 id）
    private Long eventId;         // 报的哪个赛事（对应 event 表的 id）
    private String realName;      // 报名时的真实姓名
    private String idCard;        // 报名时的身份证号
    private String shirtSize;     // 服装尺码：S / M / L / XL / XXL
    private String emergencyContact;  // 紧急联系人电话（万一比赛中出情况可以联系）

    /**
     * 审核状态：
     *   0 — 待审核（刚提交，管理员还没看）
     *   1 — 已通过（管理员审核通过了）
     *   2 — 已拒绝（管理员驳回了）
     *   3 — 已取消（用户自己取消了）
     */
    private Integer status;

    private LocalDateTime applyTime;  // 报名提交时间

    // ↓↓↓ 以下两个字段不在 registration 表中，是 SQL 多表关联查询时带出来的扩展字段 ↓↓↓
    private String eventName;   // 赛事名称（来自 event 表，用于列表显示"你报了什么比赛"）
    private String username;    // 用户名（来自 user 表，用于管理员查看报名人是谁）
}
