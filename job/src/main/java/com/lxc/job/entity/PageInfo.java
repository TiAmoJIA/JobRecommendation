package com.lxc.job.entity;

import lombok.Data;

import java.util.List;

@Data
public class PageInfo<T> {
    private List<T> jobList; // 当前页数据列表
    private long total;   // 总记录数
    private long pageNum;  // 当前页码
    private long pageSize; // 每页显示的记录数
    private long pages;    // 总页数
}
