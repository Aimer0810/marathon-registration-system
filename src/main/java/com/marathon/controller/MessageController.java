package com.marathon.controller;

import com.marathon.common.Result;
import com.marathon.entity.AiChatMessage;
import com.marathon.entity.Message;
import com.marathon.entity.User;
import com.marathon.service.AiChatMessageService;
import com.marathon.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 留言 & AI聊天记录控制器（用户端，需要登录）
 *
 * 这个控制器同时处理两个不相关的功能，主要是因为它们路径相近。
 * 严格来说这是不太好的设计，应该分成两个类，但毕业设计项目中放一起也能接受。
 */
@RestController
@RequestMapping("/api/messages")
public class MessageController extends BaseController {

    @Autowired
    private MessageService messageService;

    @Autowired
    private AiChatMessageService aiChatMessageService;

    /** 提交留言（自动关联当前登录用户） */
    @PostMapping
    public Result<String> add(@RequestBody Message message, HttpServletRequest request) {
        User user = getLoginUser(request);
        if (user == null) return Result.error(401, "请先登录");
        message.setUserId(user.getId());
        messageService.add(message);
        return Result.success("留言提交成功");
    }

    /** 查看我的留言列表（含管理员回复） */
    @GetMapping("/user")
    public Result<List<Message>> myMessages(HttpServletRequest request) {
        User user = getLoginUser(request);
        if (user == null) return Result.error(401, "请先登录");
        return Result.success(messageService.getUserMessages(user.getId()));
    }

    /** 查看我的 AI 聊天历史（供前端 AiAssistant 组件加载历史记录） */
    @GetMapping("/ai")
    public Result<List<AiChatMessage>> myAiMessages(HttpServletRequest request) {
        User user = getLoginUser(request);
        if (user == null) return Result.error(401, "请先登录");
        return Result.success(aiChatMessageService.getUserMessages(user.getId()));
    }
}
