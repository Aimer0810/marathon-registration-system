package com.marathon.controller;

import com.marathon.common.Result;
import com.marathon.entity.Registration;
import com.marathon.entity.User;
import com.marathon.service.RegistrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 报名控制器（用户端）—— 提交报名、查看我的报名、取消报名
 *
 * 所有方法都需要登录（通过 getLoginUser 检查）。
 * 报名信息中的 realName 和 idCard 自动从登录用户信息中获取，
 * 不需要前端传递——避免用户随意填写别人的身份信息。
 */
@RestController
@RequestMapping("/api")
public class RegistrationController extends BaseController {

    @Autowired
    private RegistrationService registrationService;

    /**
     * 提交报名
     *
     * 前端只需传 eventId、shirtSize、emergencyContact，
     * userId、realName、idCard 由后端从当前登录用户信息中自动填充。
     */
    @PostMapping("/registrations")
    public Result<String> apply(@RequestBody Registration registration, HttpServletRequest request) {
        User user = getLoginUser(request);
        if (user == null) return Result.error(401, "请先登录");
        boolean ok = registrationService.apply(registration, user);
        return ok ? Result.success("报名成功，等待审核") : Result.error("报名失败");
    }

    /** 查看我的报名记录（列表包含赛事名称，因为 SQL 里 LEFT JOIN 了 event 表） */
    @GetMapping("/user/registrations")
    public Result<List<Registration>> myRegistrations(HttpServletRequest request) {
        User user = getLoginUser(request);
        if (user == null) return Result.error(401, "请先登录");
        List<Registration> list = registrationService.getUserRegistrations(user.getId());
        return Result.success(list);
    }

    /**
     * 取消报名
     * 只能取消"待审核"状态的报名，已通过/已拒绝的不能取消
     */
    @DeleteMapping("/registrations/{id}")
    public Result<String> cancel(@PathVariable Long id, HttpServletRequest request) {
        User user = getLoginUser(request);
        if (user == null) return Result.error(401, "请先登录");
        boolean ok = registrationService.cancel(id, user.getId());
        return ok ? Result.success("取消成功") : Result.error("取消失败");
    }
}
