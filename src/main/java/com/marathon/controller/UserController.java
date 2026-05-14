package com.marathon.controller;

import com.marathon.common.Result;
import com.marathon.entity.User;
import com.marathon.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * 用户个人中心控制器 —— 修改资料、修改密码
 *
 * updateProfile 修改资料后，会刷新 Token 存储中的用户信息。
 * 修改密码后，为了安全会直接让用户重新登录（删除 Token）。
 */
@RestController
@RequestMapping("/api/user")
public class UserController extends BaseController {

    @Autowired
    private UserService userService;

    /**
     * 修改个人资料（真实姓名、电话、身份证号）
     *
     * 注意：前端传来的 user 对象只有前端表单填的那几个字段有值，
     * 后端会强制把 id 设成当前登录用户的 id，防止用户修改别人的资料。
     *
     * 修改后需要 refreshUser 刷新 Token 中的用户信息，
     * 否则下次通过 Token 拿到的还是旧信息。
     */
    @PutMapping("/profile")
    public Result<String> updateProfile(@RequestBody User user, HttpServletRequest request) {
        User loginUser = getLoginUser(request);
        if (loginUser == null) return Result.error(401, "请先登录");
        user.setId(loginUser.getId());  // 强制使用当前登录用户 ID
        boolean ok = userService.updateProfile(user);
        if (ok) {
            // 重新获取最新用户信息，刷新 Token 中的缓存
            User updated = userService.getUserById(loginUser.getId());
            updated.setPassword(null);
            authService.refreshUser(getToken(request), updated);
            return Result.success("资料更新成功");
        }
        return Result.error("资料更新失败");
    }

    /**
     * 修改密码
     *
     * 需要验证旧密码正确才能修改。
     * 修改成功后删除 Token（强制重新登录），
     * 这是一种安全实践——防止密码被修改后旧 Token 还能用。
     */
    @PutMapping("/password")
    public Result<String> changePassword(@RequestParam String oldPassword,
                                         @RequestParam String newPassword,
                                         HttpServletRequest request) {
        User loginUser = getLoginUser(request);
        if (loginUser == null) return Result.error(401, "请先登录");
        boolean ok = userService.changePassword(loginUser.getId(), oldPassword, newPassword);
        if (ok) {
            authService.removeToken(getToken(request));  // 强制重新登录
            return Result.success("密码修改成功，请重新登录");
        }
        return Result.error("原密码错误");
    }
}
