package com.marathon.service;

import com.marathon.entity.Registration;
import com.marathon.entity.User;
import com.marathon.entity.Event;
import com.marathon.mapper.EventMapper;
import com.marathon.mapper.RegistrationMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 报名业务服务 —— 最核心的业务逻辑所在
 *
 * 业务流程：
 *   用户报名 → apply() 方法六重校验 → 通过则插入数据库（status=0 待审核）
 *   管理员审核 → audit() 方法 → 通过/拒绝（status=1/2）
 *   用户取消 → cancel() 方法 → 只有待审核状态可以取消（status=3）
 *
 * 并发安全说明：
 *   本项目没有使用数据库锁，高并发时可能出现"超售"（名额满了还能报名）。
 *   生产环境需要在报名时加乐观锁或悲观锁。
 *   不过当前通过多层 if 判断能拦截大部分情况。
 */
@Service
public class RegistrationService {
    @Autowired
    private RegistrationMapper registrationMapper;
    @Autowired
    private EventMapper eventMapper;

    /**
     * 用户报名 —— 六重校验，任何一步不通过都不会插入数据库
     *
     * 校验链（按顺序）：
     *   1. 参数完整性：registration / user / eventId 不能为空
     *   2. 防止重复报名：同一个用户对同一赛事不能重复报（除非之前取消了）
     *   3. 赛事存在性：查 event 表确认赛事还在
     *   4. 时间窗口：当前时间必须在报名开始和截止之间
     *   5. 赛事状态：赛事必须是"报名中"（status=1）
     *   6. 名额检查：已通过人数不能超过名额上限
     *
     * 全部通过后，自动从当前登录用户信息中填充 realName 和 idCard，
     * 报名记录的 status 初始为 0（待审核）。
     */
    public boolean apply(Registration registration, User user) {
        // 第1步：参数校验
        if (registration == null || user == null || registration.getEventId() == null) {
            return false;
        }

        // 第2步：防止重复报名（排除已取消 status=3 的记录）
        if (registrationMapper.countByUserAndEvent(user.getId(), registration.getEventId()) > 0) {
            return false;
        }

        // 第3步：检查赛事是否存在
        Event event = eventMapper.selectById(registration.getEventId());
        if (event == null) {
            return false;
        }

        // 第4步：检查时间窗口
        LocalDateTime now = LocalDateTime.now();
        if (event.getRegStartTime() == null || event.getRegEndTime() == null) {
            return false;
        }
        if (now.isBefore(event.getRegStartTime()) || now.isAfter(event.getRegEndTime())) {
            return false;
        }

        // 第5步：检查赛事状态是否为"报名中"
        if (event.getStatus() == null || event.getStatus() != 1) {
            return false;
        }

        // 第6步：检查名额是否已满
        if (event.getQuota() != null && registrationMapper.countByEventId(event.getId()) >= event.getQuota()) {
            return false;
        }

        // 全部通过，填充信息并插入
        registration.setUserId(user.getId());
        registration.setRealName(user.getRealName());
        registration.setIdCard(user.getIdCard());
        return registrationMapper.insert(registration) > 0;
    }

    /**
     * 用户取消报名
     * 只有 "待审核"（status=0）状态才能取消。
     * 已通过/已拒绝/已取消的报名不能再次取消。
     *
     * 同时校验 userId——用户只能取消自己的报名，不能取消别人的。
     */
    public boolean cancel(Long id, Long userId) {
        Registration reg = registrationMapper.selectById(id);
        if (reg != null && reg.getUserId().equals(userId) && reg.getStatus() == 0) {
            return registrationMapper.updateStatus(id, 3) > 0;  // status=3 表示已取消
        }
        return false;
    }

    /**
     * 管理员审核报名
     *
     * 审核通过（status=1）时，再查一次名额是否满了。
     * 虽然在 apply() 时已经查过，但审核通过和报名之间可能有时间差，
     * 这期间其他报名可能被审核通过了，所以要再次检查名额。
     */
    public boolean audit(Long id, Integer status) {
        Registration reg = registrationMapper.selectById(id);
        if (reg == null) {
            return false;
        }

        // 审核通过时再检查一次名额
        if (status != null && status == 1 && reg.getStatus() != 1) {
            Event event = eventMapper.selectById(reg.getEventId());
            if (event != null && event.getQuota() != null) {
                int approvedCount = registrationMapper.countByEventId(reg.getEventId());
                if (approvedCount >= event.getQuota()) {
                    return false;  // 名额已满，不能通过
                }
            }
        }

        return registrationMapper.updateStatus(id, status) > 0;
    }

    /** 查询某个用户的所有报名记录（含赛事名称，用于"我的报名"页面） */
    public List<Registration> getUserRegistrations(Long userId) {
        return registrationMapper.selectByUserId(userId);
    }

    /** 管理员查询报名记录（可按赛事和状态筛选） */
    public List<Registration> getAllRegistrations(Long eventId, Integer status) {
        return registrationMapper.selectAll(eventId, status);
    }

    public Registration getById(Long id) {
        return registrationMapper.selectById(id);
    }
}
