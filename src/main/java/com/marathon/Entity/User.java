package com.marathon.entity;

import lombok.Data;  // Lombok 注解，自动生成 getter/setter/toString 等方法，不用手写

import java.time.LocalDateTime;  // Java 8 的日期时间类，用来表示 "年-月-日 时:分:秒"

/**
 * 用户实体类 —— 对应数据库中的 user 表
 *
 * 什么是实体类？
 *   实体类就是一个"数据的容器"，它的每个字段对应数据库表的一个列。
 *   比如 User 类对应 user 表，User 的 id 字段对应 user 表的 id 列。
 *   当你从数据库查出一条用户记录，MyBatis 会自动把它封装成一个 User 对象。
 *
 * 什么是 @Data？
 *   这是 Lombok（一个Java工具库）提供的注解。
 *   加了这个注解后，你不用手写 getXxx()、setXxx()、toString() 这些方法，
 *   Lombok 会在编译时自动帮你生成。这样代码更简洁。
 */
@Data
public class User {
    private Long id;             // 用户唯一编号，数据库自增主键
    private String username;     // 登录用户名，用于登录时输入
    private String password;     // 登录密码（注意：这个项目里是明文存储的，生产环境应该加密）
    private String realName;     // 真实姓名，用于报名时显示
    private String phone;        // 手机号码
    private String idCard;       // 身份证号码
    private Integer role;        // 角色：0 = 普通用户，1 = 管理员（用 Integer 而非 int 是为了能存 null）
    private Integer status;      // 账号状态：0 = 已禁用（不能登录），1 = 正常
    private LocalDateTime createTime;  // 注册时间
}
