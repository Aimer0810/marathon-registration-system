package com.marathon.service;

import com.marathon.common.PageResult;
import com.marathon.entity.Event;
import com.marathon.mapper.EventMapper;
import com.marathon.mapper.RegistrationMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 赛事业务服务 — 负责赛事的增删改查 + 状态自动流转
 *
 * 核心设计：赛事状态（未开始/报名中/已截止）不需要定时任务，
 * 而是每次查询时检查当前时间与报名时间窗口的关系，实时更新。
 * 这样做的好处是零依赖，坏处是状态更新依赖用户请求触发。
 */
@Service
public class EventService {
    @Autowired
    private EventMapper eventMapper;
    @Autowired
    private RegistrationMapper registrationMapper;

    /**
     * 校验赛事时间安排的合法性：
     * 1. 报名开始时间不能晚于结束时间
     * 2. 报名结束时间必须早于比赛日期（不能在比赛当天还在报名）
     * 3. 报名开始时间也必须早于比赛日期
     */
    private void validateEventSchedule(Event event) {
        if (event == null
                || event.getEventDate() == null
                || event.getRegStartTime() == null
                || event.getRegEndTime() == null) {
            throw new IllegalArgumentException("Event schedule information is incomplete");
        }
        LocalDate eventDate = event.getEventDate();
        LocalDate regStartDate = event.getRegStartTime().toLocalDate();
        LocalDate regEndDate = event.getRegEndTime().toLocalDate();

        if (event.getRegStartTime().isAfter(event.getRegEndTime())) {
            throw new IllegalArgumentException("Registration start time cannot be later than end time");
        }
        if (regEndDate.isAfter(eventDate) || regEndDate.isEqual(eventDate)) {
            throw new IllegalArgumentException("Registration end time must be earlier than event date");
        }
        if (regStartDate.isAfter(eventDate) || regStartDate.isEqual(eventDate)) {
            throw new IllegalArgumentException("Registration start time must be earlier than event date");
        }
    }

    // ==================== 基本 CRUD ====================

    public void addEvent(Event event) {
        validateEventSchedule(event);   // 时间合法性校验不通过会直接抛异常
        eventMapper.insert(event);
    }

    public void updateEvent(Event event) {
        validateEventSchedule(event);
        eventMapper.update(event);
    }

    public void deleteEvent(Long id) {
        eventMapper.deleteById(id);
    }

    public Event getEventById(Long id) {
        return eventMapper.selectById(id);
    }

    public List<Event> getAllEvents() {
        return eventMapper.selectAll();
    }

    /**
     * 供前端用户浏览使用——默认只展示"报名中"（status=1）的赛事
     */
    public List<Event> getEventsForUser(String keyword, Integer status) {
        return eventMapper.selectList(keyword, status != null ? status : 1);
    }

    /**
     * 分页查询——手动计算 offset 进行 MySQL LIMIT 分页
     */
    public PageResult<Event> getPage(int pageNum, int pageSize, String keyword, Integer status) {
        int offset = (pageNum - 1) * pageSize;
        List<Event> list = eventMapper.selectPage(keyword, status, offset, pageSize);
        long total = eventMapper.count(keyword, status);
        return new PageResult<>(list, total, pageNum, pageSize);
    }

    // ==================== 赛事状态自动流转 ====================

    /**
     * 核心方法：根据当前时间自动更新所有赛事的状态
     *
     * 状态规则：
     *   now < 报名开始时间        → 0（未开始）
     *   报名开始 < now < 报名截止  → 1（报名中）
     *   now > 报名截止时间        → 2（已截止）
     *
     * 特殊规则：即使处于报名时段，如果已通过人数 >= 名额上限，
     * 也会自动变为"已截止"（满额即止）。
     *
     * 注意：此方法在每次查询赛事时都会被调用，属于"懒更新"策略。
     */
    public void checkAndUpdateEventStatus() {
        List<Event> events = eventMapper.selectAll();
        LocalDateTime now = LocalDateTime.now();
        for (Event e : events) {
            int oldStatus = e.getStatus();
            int newStatus = oldStatus;   // 默认不变

            if (now.isBefore(e.getRegStartTime())) {
                newStatus = 0;   // 未开始
            } else if (now.isAfter(e.getRegStartTime()) && now.isBefore(e.getRegEndTime())) {
                newStatus = 1;   // 报名中
            } else if (now.isAfter(e.getRegEndTime())) {
                newStatus = 2;   // 已截止
            }

            // 在报名中状态下检查是否满额
            if (newStatus == 1 && e.getQuota() != null) {
                int count = registrationMapper.countByEventId(e.getId());
                if (count >= e.getQuota()) {
                    newStatus = 2;   // 满额自动截止
                }
            }

            // 只有状态真正变化时才写数据库，减少不必要的 UPDATE
            if (newStatus != oldStatus) {
                eventMapper.updateStatus(e.getId(), newStatus);
            }
        }
    }
}
