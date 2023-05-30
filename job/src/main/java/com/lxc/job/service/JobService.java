package com.lxc.job.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lxc.job.entity.Job;
import com.lxc.job.entity.PageInfo;

import java.util.List;

public interface JobService {
    PageInfo<Job> getJobByIndex(Page<Job> page,
                                String cityName,
                                Integer position,
                                Integer industry,
                                String jobExperience,
                                String salaryDesc,
                                String brandScaleName,
                                String brandStageName,
                                String jobDegree);

    void pointLog(String securityId);

    List<Job> getHotJob();
    List<Job> getRecJob(List<Integer> jobIdList);
    Integer getIdBySecurityId(String securityId);
}
