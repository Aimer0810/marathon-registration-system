package com.marathon.service;

import com.marathon.entity.User;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;                           // 生成全局唯一的随机 ID
import java.util.concurrent.ConcurrentHashMap;   // 线程安全的 HashMap（支持多线程并发访问）

/**
 * 认证服务 —— 负责 Token 的生成、存储和验证
 *
 * 什么是 Token？
 *   Token 就像是"临时身份证"。用户登录成功后，服务器发一个 Token 给客户端，
 *   之后客户端每次请求都带上这个 Token，服务器就能识别"你是谁"。
 *   类比：你去酒店入住，前台给你一张房卡(token)，之后你凭房卡进出。
 *
 * 为什么不用 Session？
 *   传统 Session 是服务器在内存中维护用户状态，但水平扩展（多台服务器）时
 *   需要共享 Session。Token 是无状态的，更适合前后端分离项目。
 *
 * 本项目的简化实现：
 *   - Token 存储在 ConcurrentHashMap 中（内存存储）
 *   - Token 格式：mtk_ + UUID（如 mtk_a1b2c3d4-...）
 *   - 服务重启后所有 Token 失效（所有用户需要重新登录）
 *   - 没有过期时间（生产环境应该加过期机制）
 *
 * sanitizeUser() 的作用：
 *   取出用户信息时创建一个副本，并故意不拷贝 password 字段。
 *   这样即使 Token 被泄露，攻击者也只能看到用户基本信息，拿不到密码。
 */
@Service
public class AuthService {
    /** Token 前缀，方便识别这是本系统生成的 Token */
    private static final String TOKEN_PREFIX = "mtk_";

    /**
     * Token 存储容器
     * ConcurrentHashMap 和普通 HashMap 的区别：
     *   HashMap 在多线程同时读写时可能出错甚至死循环
     *   ConcurrentHashMap 是线程安全的，多线程并发访问没问题
     *
     * Key: Token 字符串（如 "mtk_a1b2c3d4..."）
     * Value: 对应的用户信息（已脱敏，不含密码）
     */
    private final Map<String, User> tokenStore = new ConcurrentHashMap<>();

    /**
     * 创建 Token（用户登录时调用）
     * UUID.randomUUID() 生成一个全球唯一的随机字符串，
     * 碰撞概率极低（理论上需要生成数十亿个才会重复）。
     */
    public String createToken(User user) {
        String token = TOKEN_PREFIX + UUID.randomUUID();
        tokenStore.put(token, sanitizeUser(user));  // 存入副本，不存密码
        return token;
    }

    /**
     * 根据 Token 获取用户（每次请求时调用）
     * @return 找到了返回用户信息，找不到返回 null（表示未登录或 Token 过期）
     */
    public User getUserByToken(String token) {
        if (token == null || token.trim().isEmpty()) {
            return null;
        }
        User user = tokenStore.get(token);
        return user == null ? null : sanitizeUser(user);  // 返回副本，防止外部修改
    }

    /**
     * 刷新 Token 对应的用户信息（用户修改资料后调用）
     * 比如修改了真实姓名，下次通过 Token 拿到的最新信息就是更新后的。
     */
    public void refreshUser(String token, User user) {
        if (token == null || token.trim().isEmpty() || user == null) {
            return;
        }
        tokenStore.put(token, sanitizeUser(user));
    }

    /** 删除 Token（退出登录时调用） */
    public void removeToken(String token) {
        if (token == null || token.trim().isEmpty()) {
            return;
        }
        tokenStore.remove(token);
    }

    /**
     * 用户信息脱敏——创建一个不含密码的用户副本
     *
     * 为什么要这样做？
     *   数据库中 user 对象有 password 字段，如果直接把原始对象存到 tokenStore，
     *   后续通过 Token 拿到用户信息时，password 字段也暴露在内存中。
     *   虽然这个 password 不会被序列化返回给前端（因为 LoginController 手动 setPassword(null)），
     *   但以防万一，在存储层面就过滤掉密码更安全。
     */
    private User sanitizeUser(User user) {
        User copy = new User();
        copy.setId(user.getId());
        copy.setUsername(user.getUsername());
        // 注意：故意不拷贝 password 字段
        copy.setRealName(user.getRealName());
        copy.setPhone(user.getPhone());
        copy.setIdCard(user.getIdCard());
        copy.setRole(user.getRole());
        copy.setStatus(user.getStatus());
        copy.setCreateTime(user.getCreateTime());
        return copy;
    }
}
