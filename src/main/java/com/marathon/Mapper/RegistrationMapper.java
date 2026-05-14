package com.marathon.mapper;

import com.marathon.entity.Registration;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

/**
 * 报名数据访问接口
 *
 * countByEventId：统计某个赛事已通过审核的报名人数（用于判断是否满额）
 * countByUserAndEvent：查用户是否已报名某个赛事（防止重复报名，排除已取消的）
 */
@Mapper
public interface RegistrationMapper {
    int insert(Registration registration);                              // 提交报名
    int updateStatus(@Param("id") Long id, @Param("status") Integer status); // 修改报名状态（审核/取消）
    Registration selectById(Long id);                                   // 按 ID 查报名记录
    List<Registration> selectByUserId(Long userId);                     // 查某个用户的所有报名
    List<Registration> selectAll(@Param("eventId") Long eventId,        // 管理员查所有报名（可按赛事和状态筛选）
                                 @Param("status") Integer status);
    int countByEventId(Long eventId);                                   // 统计某赛事已通过的人数（status=1）
    int countByUserAndEvent(@Param("userId") Long userId,               // 查用户是否已报这个赛事（排除已取消）
                            @Param("eventId") Long eventId);
}
