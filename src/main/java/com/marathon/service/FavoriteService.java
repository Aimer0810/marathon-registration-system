package com.marathon.service;

import com.marathon.entity.Favorite;
import com.marathon.mapper.FavoriteMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

/**
 * 收藏业务服务
 *
 * 用户可以对感兴趣的赛事点"收藏"，之后在"我的收藏"页面快速查看。
 *
 * add() 中有防重复逻辑：如果已经收藏过了，再次收藏不会报错，只是返回 false。
 * remove() 通过 userId + eventId 来删除，确保用户只能删自己的收藏。
 */
@Service
public class FavoriteService {
    @Autowired
    private FavoriteMapper favoriteMapper;

    /** 收藏赛事（先查是否已收藏，防止重复） */
    public boolean add(Favorite favorite) {
        Favorite exist = favoriteMapper.selectByUserAndEvent(favorite.getUserId(), favorite.getEventId());
        if (exist != null) return false;  // 已收藏，不再重复添加
        return favoriteMapper.insert(favorite) > 0;
    }

    /** 取消收藏（按 userId + eventId 精确删除） */
    public boolean remove(Long userId, Long eventId) {
        return favoriteMapper.delete(userId, eventId) > 0;
    }

    /** 查看我的收藏列表 */
    public List<Favorite> getUserFavorites(Long userId) {
        return favoriteMapper.selectByUserId(userId);
    }

    /** 检查某用户是否已收藏某赛事（前端判断按钮显示"收藏"还是"取消收藏"） */
    public boolean isFavorited(Long userId, Long eventId) {
        return favoriteMapper.selectByUserAndEvent(userId, eventId) != null;
    }
}
