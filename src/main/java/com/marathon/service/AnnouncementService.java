package com.marathon.service;

import com.marathon.common.PageResult;
import com.marathon.entity.Announcement;
import com.marathon.mapper.AnnouncementMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 公告业务服务 —— 简单的增删改查，没有复杂业务逻辑
 *
 * 公告的数据流向：
 *   管理员发布 → INSERT 到数据库 → 所有用户在首页/公告列表页看到
 *   管理员编辑 → UPDATE
 *   管理员删除 → DELETE
 */
@Service
public class AnnouncementService {

    @Autowired
    private AnnouncementMapper announcementMapper;

    /** 获取全部公告（不分页，AI 上下文和首页用） */
    public List<Announcement> getAll() {
        return announcementMapper.selectAll();
    }

    /** 分页查询公告 */
    public PageResult<Announcement> getPage(int pageNum, int pageSize) {
        int offset = (pageNum - 1) * pageSize;
        List<Announcement> list = announcementMapper.selectPage(offset, pageSize);
        long total = announcementMapper.count();
        return new PageResult<>(list, total, pageNum, pageSize);
    }

    public Announcement getById(Long id) {
        return announcementMapper.selectById(id);
    }

    public void add(Announcement announcement) {
        announcementMapper.insert(announcement);
    }

    public void update(Announcement announcement) {
        announcementMapper.update(announcement);
    }

    public void delete(Long id) {
        announcementMapper.deleteById(id);
    }
}
