package com.marathon.controller;

import com.marathon.common.Result;
import com.marathon.entity.Event;
import com.marathon.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 管理员-赛事管理控制器
 *
 * 所有接口都需要管理员权限（角色 role=1）。
 * isAdmin() 方法来自父类 BaseController，通过当前登录用户的 role 判断。
 * 如果不是管理员，返回 403 "无权限"。
 *
 * 为什么要把管理员接口单独成类？
 *   1. 职责分离——管理员操作和普通用户操作分开，代码更清晰
 *   2. 权限控制——每个方法开头统一检查 isAdmin()
 *   3. 路径规范——所有管理接口都在 /api/admin/ 下
 *
 * add/update 方法捕获 IllegalArgumentException：
 *   这是 EventService.validateEventSchedule() 在时间安排不合法时抛出的异常，
 *   比如"报名开始时间晚于比赛日期"，前端会显示具体的错误信息。
 */
@RestController
@RequestMapping("/api/admin/events")
public class AdminEventController extends BaseController {

    @Autowired
    private EventService eventService;

    /** 获取全部赛事（管理员看的不需要分页） */
    @GetMapping
    public Result<List<Event>> list(HttpServletRequest request) {
        if (!isAdmin(request)) return Result.error(403, "无权限");
        return Result.success(eventService.getAllEvents());
    }

    /** 新增赛事 */
    @PostMapping
    public Result<String> add(@RequestBody Event event, HttpServletRequest request) {
        if (!isAdmin(request)) return Result.error(403, "无权限");
        try {
            eventService.addEvent(event);
        } catch (IllegalArgumentException e) {
            return Result.error(e.getMessage());  // 返回具体的校验错误信息
        }
        return Result.success("新增成功");
    }

    /** 编辑赛事 */
    @PutMapping("/{id}")
    public Result<String> update(@PathVariable Long id, @RequestBody Event event,
                                  HttpServletRequest request) {
        if (!isAdmin(request)) return Result.error(403, "无权限");
        event.setId(id);  // 从 URL 提取 id，设置到 event 对象上
        try {
            eventService.updateEvent(event);
        } catch (IllegalArgumentException e) {
            return Result.error(e.getMessage());
        }
        return Result.success("修改成功");
    }

    /** 删除赛事 */
    @DeleteMapping("/{id}")
    public Result<String> delete(@PathVariable Long id, HttpServletRequest request) {
        if (!isAdmin(request)) return Result.error(403, "无权限");
        eventService.deleteEvent(id);
        return Result.success("删除成功");
    }
}
