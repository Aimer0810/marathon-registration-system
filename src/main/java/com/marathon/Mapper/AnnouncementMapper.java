package com.marathon.mapper;

import com.marathon.entity.Announcement;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

/**
 * 公告数据访问接口
 */
@Mapper
public interface AnnouncementMapper {
    int insert(Announcement announcement);                           // 发布公告
    int update(Announcement announcement);                           // 编辑公告
    int deleteById(Long id);                                         // 删除公告
    Announcement selectById(Long id);                                // 按 ID 查公告
    List<Announcement> selectAll();                                  // 查所有公告
    List<Announcement> selectPage(@Param("offset") int offset,       // 分页查公告
                                  @Param("size") int size);
    long count();                                                    // 公告总数
}
