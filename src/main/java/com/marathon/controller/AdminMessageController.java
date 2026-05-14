package com.marathon.controller;

import com.marathon.common.Result;
import com.marathon.entity.Message;
import com.marathon.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 管理员-留言管理控制器
 *
 * 管理员可以查看所有用户的留言并进行回复。
 * 回复后用户在"我的留言"页面就能看到管理员的回复内容。
 */
@RestController
@RequestMapping("/api/admin/messages")
public class AdminMessageController extends BaseController {

    @Autowired
    private MessageService messageService;

    /** 查看所有留言 */
    @GetMapping
    public Result<List<Message>> list(HttpServletRequest request) {
        if (!isAdmin(request)) return Result.error(403, "无权限");
        return Result.success(messageService.getAllMessages());
    }

    /** 回复某条留言 */
    @PutMapping("/{id}/reply")
    public Result<String> reply(@PathVariable Long id,
                                 @RequestParam String reply,
                                 HttpServletRequest request) {
        if (!isAdmin(request)) return Result.error(403, "无权限");
        messageService.reply(id, reply);
        return Result.success("回复成功");
    }
}
