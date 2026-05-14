package com.marathon.mapper;

import com.marathon.entity.Event;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

/**
 * 赛事数据访问接口
 *
 * 注意 selectPage 方法：自己手动分页，传入 offset（偏移量）和 size（每页条数），
 * 对应 SQL 的 LIMIT #{offset}, #{size}。
 *
 * count 方法用于计算总记录数，配合分页使用。
 * 前端分页组件需要知道"一共有多少条"才能显示正确页码。
 */
@Mapper
public interface EventMapper {
    int insert(Event event);                                              // 新增赛事
    int update(Event event);                                              // 更新赛事信息
    int deleteById(Long id);                                              // 删除赛事
    Event selectById(Long id);                                            // 按 ID 查单个赛事
    List<Event> selectAll();                                              // 查所有赛事
    List<Event> selectList(@Param("keyword") String keyword,              // 按条件查列表（不限条数）
                           @Param("status") Integer status);
    List<Event> selectPage(@Param("keyword") String keyword,              // 分页查（LIMIT）
                           @Param("status") Integer status,
                           @Param("offset") int offset,
                           @Param("size") int size);
    long count(@Param("keyword") String keyword,                          // 统计总数（分页用）
               @Param("status") Integer status);
    int updateStatus(@Param("id") Long id, @Param("status") Integer status); // 只更新状态
}
