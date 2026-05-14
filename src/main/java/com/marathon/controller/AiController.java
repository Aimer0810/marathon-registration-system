package com.marathon.controller;

import com.marathon.common.Result;
import com.marathon.dto.AiChatRequest;
import com.marathon.dto.AiChatHistoryItem;
import com.marathon.dto.AiChatResponse;
import com.marathon.entity.AiChatMessage;
import com.marathon.entity.User;
import com.marathon.service.AiService;
import com.marathon.service.AiChatMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api/ai")
public class AiController extends BaseController {

    @Autowired
    private AiService aiService;

    @Autowired
    private AiChatMessageService aiChatMessageService;

    @PostMapping(value = "/chat", produces = "application/json;charset=UTF-8")
    public Result<AiChatResponse> chat(@RequestBody AiChatRequest request, HttpServletRequest httpServletRequest) {
        User currentUser = getLoginUser(httpServletRequest);
        if (currentUser == null) {
            return Result.error(401, "请先登录后再使用 AI 助手");
        }
        String answer = aiService.chat(request.getMessage(), currentUser);
        aiChatMessageService.addMessage(currentUser.getId(), null, request.getMessage(), answer);
        return Result.success(new AiChatResponse(answer));
    }

    @GetMapping("/history")
    public Result<List<AiChatHistoryItem>> history(HttpServletRequest httpServletRequest) {
        User currentUser = getLoginUser(httpServletRequest);
        if (currentUser == null) {
            return Result.error(401, "请先登录后再查看 AI 对话记录");
        }

        List<AiChatMessage> records = aiChatMessageService.getUserMessages(currentUser.getId());
        List<AiChatHistoryItem> items = new ArrayList<>();
        Collections.reverse(records);
        for (AiChatMessage record : records) {
            items.add(new AiChatHistoryItem("user", record.getQuestion(), record.getCreateTime()));
            items.add(new AiChatHistoryItem("assistant", record.getAnswer(), record.getCreateTime()));
        }
        return Result.success(items);
    }
}
