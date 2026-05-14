package com.marathon.controller;

import com.marathon.common.PageResult;
import com.marathon.common.Result;
import com.marathon.entity.Announcement;
import com.marathon.service.AnnouncementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 公告控制器（公开接口，不需要登录）
 *
 * 所有用户都能看公告，包括未登录的游客。
 */
@RestController
@RequestMapping("/api/announcements")
public class AnnouncementController {

    @Autowired
    private AnnouncementService announcementService;

    /** 公告列表（支持分页或全部） */
    @GetMapping
    public Result<?> list(@RequestParam(defaultValue = "1") int pageNum,
                          @RequestParam(defaultValue = "10") int pageSize,
                          @RequestParam(required = false) Boolean all) {
        if (all != null && all) {
            return Result.success(announcementService.getAll());
        }
        PageResult<Announcement> page = announcementService.getPage(pageNum, pageSize);
        return Result.success(page);
    }

    /** 公告详情 */
    @GetMapping("/{id}")
    public Result<Announcement> detail(@PathVariable Long id) {
        return Result.success(announcementService.getById(id));
    }
}
