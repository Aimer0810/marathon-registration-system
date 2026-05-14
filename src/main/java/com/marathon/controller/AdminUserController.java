package com.marathon.controller;

import com.marathon.common.Result;
import com.marathon.entity.User;
import com.marathon.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 管理员-用户管理控制器
 *
 * 管理员可以：
 *   - 查看所有普通用户的列表（支持搜索）
 *   - 启用/禁用用户账号
 *   - 重置用户密码
 *
 * 注意：list 方法返回前手动清除了每个用户的密码字段，防止泄露。
 */
@RestController
@RequestMapping("/api/admin/users")
public class AdminUserController extends BaseController {

    @Autowired
    private UserService userService;

    /** 用户列表（默认只查 role=0 的普通用户） */
    @GetMapping
    public Result<List<User>> list(@RequestParam(required = false) String keyword,
                                    HttpServletRequest request) {
        if (!isAdmin(request)) return Result.error(403, "无权限");
        List<User> users = userService.getUserList(keyword);
        users.forEach(u -> u.setPassword(null));  // 批量清空密码，防止泄露
        return Result.success(users);
    }

    /** 启用/禁用用户（status: 1=正常, 0=禁用） */
    @PutMapping("/{id}/status")
    public Result<String> updateStatus(@PathVariable Long id,
                                        @RequestParam Integer status,
                                        HttpServletRequest request) {
        if (!isAdmin(request)) return Result.error(403, "无权限");
        userService.updateUserStatus(id, status);
        return Result.success("操作成功");
    }

    /** 重置用户密码（管理员直接设置新密码，不需要知道旧密码） */
    @PostMapping("/{id}/reset-password")
    public Result<String> resetPassword(@PathVariable Long id,
                                         @RequestParam String password,
                                         HttpServletRequest request) {
        if (!isAdmin(request)) return Result.error(403, "无权限");
        userService.resetPassword(id, password);
        return Result.success("密码重置成功");
    }
}
