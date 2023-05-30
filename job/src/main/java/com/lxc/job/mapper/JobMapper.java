package com.lxc.job.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lxc.job.entity.Job;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface JobMapper extends BaseMapper<Job> {
//    List<Job> selectByIndex(@Param("index") String index);
}
