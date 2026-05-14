package com.marathon.controller;

import com.marathon.common.Result;
import com.marathon.entity.Registration;
import com.marathon.service.RegistrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 管理员-报名审核控制器
 *
 * 管理员可以：
 *   - 查看所有报名记录（可按赛事和状态筛选）
 *   - 审核报名（通过/拒绝）
 *
 * audit 接口接收的参数 status：
 *   1 = 审核通过
 *   2 = 审核拒绝
 */
@RestController
@RequestMapping("/api/admin/registrations")
public class AdminRegistrationController extends BaseController {

    @Autowired
    private RegistrationService registrationService;

    /** 查看报名记录（可按赛事ID和状态筛选） */
    @GetMapping
    public Result<List<Registration>> list(@RequestParam(required = false) Long eventId,
                                           @RequestParam(required = false) Integer status,
                                           HttpServletRequest request) {
        if (!isAdmin(request)) return Result.error(403, "无权限");
        return Result.success(registrationService.getAllRegistrations(eventId, status));
    }

    /**
     * 审核报名
     * audit() 方法内部会对通过操作再次检查名额，防止超审。
     */
    @PutMapping("/{id}/audit")
    public Result<String> audit(@PathVariable Long id,
                                 @RequestParam Integer status,
                                 HttpServletRequest request) {
        if (!isAdmin(request)) return Result.error(403, "无权限");
        if (!registrationService.audit(id, status)) {
            return Result.error("审核失败");  // 可能因为名额满了导致审核通过失败
        }
        return Result.success("审核完成");
    }
}
