package com.marathon.common;

import java.util.List;

/**
 * 分页查询结果类 —— 专门用于返回列表数据 + 分页信息
 *
 * 泛型 &lt;T&gt; 表示列表中元素的类型。
 * 比如 PageResult&lt;Event&gt; 就是"一页赛事数据"。
 *
 * 前端可以用这些字段来渲染分页组件（Element Plus 的 el-pagination 组件需要这些数据）。
 */
public class PageResult<T> {
    private List<T> list;     // 当前页的数据列表（比如第1页的10条赛事）
    private long total;       // 符合条件的总记录数（比如总共 53 条赛事）
    private int pageNum;      // 当前页码（从 1 开始）
    private int pageSize;     // 每页显示多少条

    public PageResult() {}

    public PageResult(List<T> list, long total, int pageNum, int pageSize) {
        this.list = list;
        this.total = total;
        this.pageNum = pageNum;
        this.pageSize = pageSize;
    }

    public List<T> getList() { return list; }
    public void setList(List<T> list) { this.list = list; }
    public long getTotal() { return total; }
    public void setTotal(long total) { this.total = total; }
    public int getPageNum() { return pageNum; }
    public void setPageNum(int pageNum) { this.pageNum = pageNum; }
    public int getPageSize() { return pageSize; }
    public void setPageSize(int pageSize) { this.pageSize = pageSize; }
}
