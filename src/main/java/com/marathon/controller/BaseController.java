package com.marathon.controller;

import com.marathon.entity.User;
import com.marathon.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;

/**
 * 控制器基类 —— 所有需要登录态校验的 Controller 都继承它
 *
 * 继承的作用：
 *   子类（如 LoginController、UserController）可以直接使用
 *   getLoginUser()、getToken()、isAdmin() 这些方法，不用重复写。
 *
 * Token 提取逻辑：
 *   前端在请求头里带：Authorization: Bearer mtk_xxxxxxxx
 *   本类从请求头中提取 "Bearer " 后面的 token 字符串。
 *
 * 为什么不用 Spring Security？
 *   为了简单。Spring Security 功能强大但配置复杂，对于毕业设计级别的项目，
 *   自己写 BaseController 继承更直观，代码量少、容易理解。
 */
public abstract class BaseController {
    /** 请求头名称：HTTP 标准认证头 */
    private static final String AUTH_HEADER = "Authorization";
    /** Bearer Token 前缀 */
    private static final String TOKEN_PREFIX = "Bearer ";

    @Autowired
    protected AuthService authService;  // 子类可以直接用

    /**
     * 从当前请求中获取登录用户
     * @return 登录用户对象（密码已脱敏），未登录返回 null
     */
    protected User getLoginUser(HttpServletRequest request) {
        String token = getToken(request);
        return authService.getUserByToken(token);
    }

    /**
     * 从请求头提取 Token
     * 格式：Authorization: Bearer mtk_a1b2c3d4-...
     * 提取结果：mtk_a1b2c3d4-...
     */
    protected String getToken(HttpServletRequest request) {
        String header = request.getHeader(AUTH_HEADER);
        if (header == null || header.trim().isEmpty()) {
            return null;
        }
        // 判断是否以 "Bearer " 开头（忽略大小写），是则截掉前缀
        return header.startsWith(TOKEN_PREFIX) ? header.substring(TOKEN_PREFIX.length()) : header;
    }

    /**
     * 判断当前用户是否为管理员
     * role = 1 表示管理员，role = 0 表示普通用户
     */
    protected boolean isAdmin(HttpServletRequest request) {
        User user = getLoginUser(request);
        return user != null && user.getRole() != null && user.getRole() == 1;
    }
}
