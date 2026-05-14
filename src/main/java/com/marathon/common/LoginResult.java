package com.marathon.common;

import com.marathon.entity.User;

/**
 * 登录成功后返回给前端的结果对象
 *
 * 包含两个信息：
 *   1. token — 身份令牌，前端存到 sessionStorage，以后每次请求都带上它来证明身份
 *   2. user  — 当前登录用户的基本信息（密码已经在上层被置为 null 了）
 *
 * 前端收到后调用 auth.js 里的 setAuth() 保存这两个信息。
 */
public class LoginResult {
    private String token;   // 身份令牌（UUID 字符串，前缀 mtk_）
    private User user;      // 登录用户信息（密码字段已清空）

    public LoginResult() {}

    public LoginResult(String token, User user) {
        this.token = token;
        this.user = user;
    }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
}
