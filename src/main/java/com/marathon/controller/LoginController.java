package com.marathon.controller;

import com.marathon.common.LoginResult;
import com.marathon.common.Result;
import com.marathon.entity.User;
import com.marathon.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * 登录/注册控制器 —— 公开接口，不需要登录
 *
 * 什么是 @RestController？
 *   = @Controller + @ResponseBody
 *   表示这个类中的所有方法返回值都会直接序列化为 JSON 返回给前端。
 *
 * 什么是 @RequestMapping("/api")？
 *   定义这个控制器处理的基础路径，所有方法路径都会拼在 /api 后面。
 *   比如 @PostMapping("/login") → 完整路径是 /api/login
 *
 * 什么是 @PostMapping / @GetMapping？
 *   @PostMapping("/xxx") = 只接受 POST 请求访问 /xxx
 *   @GetMapping("/xxx")  = 只接受 GET 请求访问 /xxx
 *
 * 什么是 @RequestBody？
 *   告诉 Spring：把 HTTP 请求的 JSON Body 自动转换成 Java 对象。
 *   比如前端发 {"username":"admin","password":"123"}，
 *   Spring 会自动 new User() 然后调用 setUsername("admin") 和 setPassword("123")。
 */
@RestController
@RequestMapping("/api")
public class LoginController extends BaseController {

    @Autowired
    private UserService userService;

    /**
     * 登录接口
     *
     * 流程：
     *   1. 接收前端传来的 username + password（JSON 格式）
     *   2. 调用 UserService.login() 验证
     *   3. 验证通过 → 生成 Token → 把 token + 用户信息一起返回
     *   4. 验证失败 → 返回错误信息
     *
     * 注意：返回前手动 user.setPassword(null) 清除密码，
     * 防止密码被序列化到 JSON 中返回给前端。
     */
    @PostMapping("/login")
    public Result<LoginResult> login(@RequestBody User loginUser) {
        User user = userService.login(loginUser.getUsername(), loginUser.getPassword());
        if (user != null) {
            user.setPassword(null);  // 返回前端前清空密码字段
            String token = authService.createToken(user);  // 生成 Token（mtk_ + UUID）
            return Result.success(new LoginResult(token, user));
        }
        return Result.error("用户名或密码错误");
    }

    /**
     * 注册接口
     *
     * 前端发来的 JSON 包含 username、password、realName、phone、idCard
     * Spring 自动转换为 User 对象
     */
    @PostMapping("/register")
    public Result<String> register(@RequestBody User user) {
        boolean success = userService.register(user);
        if (success) {
            return Result.success("注册成功");
        }
        return Result.error("用户名已存在");
    }

    /**
     * 退出登录
     *
     * 从 tokenStore 中删除这个 token，之后这个 token 就失效了。
     * 前端收到响应后也会清除本地存储的 token 和用户信息。
     */
    @GetMapping("/logout")
    public Result<String> logout(HttpServletRequest request) {
        authService.removeToken(getToken(request));
        return Result.success("退出成功");
    }

    /**
     * 获取当前登录用户信息
     *
     * 前端在页面刷新时会调用这个接口，根据存储的 token 恢复用户信息。
     * 如果 token 有效就返回用户信息，无效则返回"未登录"。
     */
    @GetMapping("/current-user")
    public Result<User> getCurrentUser(HttpServletRequest request) {
        User user = getLoginUser(request);
        if (user != null) {
            user.setPassword(null);
            return Result.success(user);
        }
        return Result.error("未登录");
    }
}
