package com.marathon.mapper;

import com.marathon.entity.Favorite;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

/**
 * 收藏数据访问接口
 *
 * delete：取消收藏（按用户ID+赛事ID删除，确保只能删自己的收藏）
 * selectByUserAndEvent：检查某用户是否已收藏某赛事
 */
@Mapper
public interface FavoriteMapper {
    int insert(Favorite favorite);                                          // 收藏赛事
    int delete(@Param("userId") Long userId, @Param("eventId") Long eventId); // 取消收藏
    List<Favorite> selectByUserId(Long userId);                             // 查看我的收藏列表
    Favorite selectByUserAndEvent(@Param("userId") Long userId,             // 检查是否已收藏
                                  @Param("eventId") Long eventId);
}
