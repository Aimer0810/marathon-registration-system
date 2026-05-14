package com.marathon.controller;

import com.marathon.common.Result;
import com.marathon.entity.Favorite;
import com.marathon.entity.User;
import com.marathon.service.FavoriteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 收藏控制器（用户端，需要登录）
 *
 * check 接口比较特殊：未登录时返回 false 而不是 401 错误。
 * 这样设计是因为游客在赛事详情页也能看到收藏按钮，
 * 如果返回 401 会触发前端的自动跳转登录，体验不好。
 */
@RestController
@RequestMapping("/api/favorites")
public class FavoriteController extends BaseController {

    @Autowired
    private FavoriteService favoriteService;

    /** 收藏赛事 */
    @PostMapping
    public Result<String> add(@RequestParam Long eventId, HttpServletRequest request) {
        User user = getLoginUser(request);
        if (user == null) return Result.error(401, "请先登录");
        Favorite fav = new Favorite();
        fav.setUserId(user.getId());
        fav.setEventId(eventId);
        boolean ok = favoriteService.add(fav);
        return ok ? Result.success("收藏成功") : Result.error("已收藏该赛事");
    }

    /** 取消收藏 */
    @DeleteMapping
    public Result<String> remove(@RequestParam Long eventId, HttpServletRequest request) {
        User user = getLoginUser(request);
        if (user == null) return Result.error(401, "请先登录");
        favoriteService.remove(user.getId(), eventId);
        return Result.success("取消收藏成功");
    }

    /** 我的收藏列表 */
    @GetMapping("/user")
    public Result<List<Favorite>> myFavorites(HttpServletRequest request) {
        User user = getLoginUser(request);
        if (user == null) return Result.error(401, "请先登录");
        return Result.success(favoriteService.getUserFavorites(user.getId()));
    }

    /**
     * 检查是否已收藏某赛事
     * 未登录时返回 false 而不是 401（方便游客查看赛事详情时按钮正常显示）
     */
    @GetMapping("/check")
    public Result<Boolean> check(@RequestParam Long eventId, HttpServletRequest request) {
        User user = getLoginUser(request);
        if (user == null) return Result.success(false);  // 游客直接返回 false
        return Result.success(favoriteService.isFavorited(user.getId(), eventId));
    }
}
