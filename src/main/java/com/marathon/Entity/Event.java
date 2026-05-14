package com.marathon.entity;

import com.fasterxml.jackson.annotation.JsonFormat;  // Jackson 注解，控制 JSON 序列化时的日期格式
import lombok.Data;

import java.math.BigDecimal;     // 精确的十进制数，专门用于表示金额（float/double 计算价格会有精度问题）
import java.time.LocalDate;      // 只表示日期（年-月-日），不包含时间
import java.time.LocalDateTime;  // 表示日期+时间（年-月-日 时:分:秒）

/**
 * 赛事实体类 —— 对应数据库中的 event 表
 *
 * 每一场比赛（比如"2026北京马拉松"）就是一条 Event 记录。
 * 赛事有 4 种状态，由系统根据当前时间和报名窗口自动流转。
 */
@Data
public class Event {
    private Long id;             // 赛事唯一编号

    private String name;         // 赛事名称，如"2026北京马拉松"
    private String location;     // 比赛地点，如"北京市天安门广场"

    // @JsonFormat 的作用：当这个对象被转换成 JSON 返回给前端时，
    // 日期会按指定格式输出，比如 "2026-10-18"
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private LocalDate eventDate;        // 比赛日期（只有日期，没有具体时刻）

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime regStartTime; // 报名开始时间（精确到秒）

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime regEndTime;   // 报名截止时间（精确到秒）

    private Integer quota;              // 名额上限，比如 30000（人）。为 null 表示不限制
    private BigDecimal entryFee;        // 报名费用，用 BigDecimal 避免浮点数精度问题（如 199.99 元）
    private String description;         // 赛事详细介绍（HTML 格式，可以包含图片等富文本）

    /**
     * 赛事状态（由系统自动计算，不需要手动设置）：
     *   0 — 未开始（还没到报名时间）
     *   1 — 报名中（可以报名）
     *   2 — 已截止（报名结束了，或者名额满了）
     *   3 — 已结束（比赛已经比完了）
     */
    private Integer status;

    private Integer registeredCount;    // 已报名人数（通过 SQL 子查询实时统计，不是数据库里存的字段）
    private LocalDateTime createTime;   // 这条赛事记录的创建时间
}
