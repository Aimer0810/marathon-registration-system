package com.marathon.controller;

import com.marathon.common.Result;
import com.marathon.entity.Announcement;
import com.marathon.entity.User;
import com.marathon.service.AnnouncementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 管理员-公告管理控制器
 *
 * 发布公告时自动记录发布人 ID（publisherId），便于追溯。
 */
@RestController
@RequestMapping("/api/admin/announcements")
public class AdminAnnouncementController extends BaseController {

    @Autowired
    private AnnouncementService announcementService;

    /** 公告列表（全部，不分页） */
    @GetMapping
    public Result<List<Announcement>> list(HttpServletRequest request) {
        if (!isAdmin(request)) return Result.error(403, "无权限");
        return Result.success(announcementService.getAll());
    }

    /** 发布新公告，自动设置发布人 ID */
    @PostMapping
    public Result<String> add(@RequestBody Announcement announcement, HttpServletRequest request) {
        if (!isAdmin(request)) return Result.error(403, "无权限");
        User user = getLoginUser(request);
        announcement.setPublisherId(user.getId());  // 自动记录发布人
        announcementService.add(announcement);
        return Result.success("发布成功");
    }

    @PutMapping("/{id}")
    public Result<String> update(@PathVariable Long id,
                                  @RequestBody Announcement announcement,
                                  HttpServletRequest request) {
        if (!isAdmin(request)) return Result.error(403, "无权限");
        announcement.setId(id);
        announcementService.update(announcement);
        return Result.success("修改成功");
    }

    @DeleteMapping("/{id}")
    public Result<String> delete(@PathVariable Long id, HttpServletRequest request) {
        if (!isAdmin(request)) return Result.error(403, "无权限");
        announcementService.delete(id);
        return Result.success("删除成功");
    }
}
