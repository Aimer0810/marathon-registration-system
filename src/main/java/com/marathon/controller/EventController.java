package com.marathon.controller;

import com.marathon.common.PageResult;
import com.marathon.common.Result;
import com.marathon.entity.Event;
import com.marathon.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 赛事控制器（用户端）—— 浏览赛事列表和详情
 *
 * 为什么不需要登录？
 *   游客也可以浏览赛事，只有报名时才需要登录。
 *
 * 什么是 @PathVariable？
 *   把 URL 路径中的参数映射到方法参数。
 *   比如 GET /api/events/5 → @PathVariable Long id 的值为 5
 *
 * 什么是 @RequestParam？
 *   把 URL 查询参数映射到方法参数。
 *   比如 GET /api/events?keyword=北京&status=1
 *   → keyword = "北京", status = 1
 *
 *   参数说明：
 *     required = false — 这个参数可以传也可以不传
 *     defaultValue = "1" — 不传时的默认值
 */
@RestController
@RequestMapping("/api/events")
public class EventController {

    @Autowired
    private EventService eventService;

    /**
     * 赛事列表（支持搜索、筛选、分页）
     *
     * all=true 时返回全部赛事（不分页），用于首页展示和 AI 上下文
     * all 不传或=false 时走分页查询
     *
     * 每次查询前都会调用 checkAndUpdateEventStatus() 刷新状态，
     * 确保返回的状态是最新的（比如刚过截止时间的赛事会从"报名中"变为"已截止"）。
     */
    @GetMapping
    public Result<?> list(@RequestParam(required = false) String keyword,      // 搜索关键字（模糊匹配赛事名称）
                          @RequestParam(required = false) Integer status,     // 状态筛选
                          @RequestParam(required = false) Boolean all,         // 是否返回全部
                          @RequestParam(defaultValue = "1") int pageNum,       // 页码
                          @RequestParam(defaultValue = "10") int pageSize) {   // 每页条数
        eventService.checkAndUpdateEventStatus();
        if (all != null && all) {
            return Result.success(eventService.getAllEvents());
        }
        PageResult<Event> page = eventService.getPage(pageNum, pageSize, keyword, status);
        return Result.success(page);
    }

    /**
     * 赛事详情
     * 比如 GET /api/events/3 返回 id=3 的赛事的完整信息
     */
    @GetMapping("/{id}")
    public Result<Event> detail(@PathVariable Long id) {
        Event event = eventService.getEventById(id);
        if (event != null) {
            return Result.success(event);
        }
        return Result.error("赛事不存在");
    }
}
