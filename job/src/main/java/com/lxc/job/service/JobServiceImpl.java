package com.lxc.job.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lxc.job.entity.Job;
import com.lxc.job.entity.PageInfo;
import com.lxc.job.mapper.JobMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class JobServiceImpl implements JobService{
    @Autowired
    private JobMapper jobMapper;



    @Override
    public PageInfo<Job> getJobByIndex(Page<Job> page,
                                       String cityName,
                                       Integer position,
                                       Integer industry,
                                       String jobExperience,
                                       String salaryDesc,
                                       String brandScaleName,
                                       String brandStageName,
                                       String jobDegree){
//        String patternStr = "[\\u4e00-\\u9fa5]+";
        QueryWrapper<Job> queryWrapper = new QueryWrapper<>();
        Optional.ofNullable(position).ifPresent(key -> queryWrapper.eq("position", key));
        Optional.ofNullable(cityName).ifPresent(key -> queryWrapper.eq("cityName", key));
        Optional.ofNullable(industry).ifPresent(key -> queryWrapper.eq("industry", key));
        Optional.ofNullable(jobExperience).ifPresent(key -> queryWrapper.eq("jobExperience", key));
        Optional.ofNullable(salaryDesc).ifPresent(key -> queryWrapper.like("salaryDesc", key));
        Optional.ofNullable(brandScaleName).ifPresent(key -> queryWrapper.eq("brandScaleName", key));
        Optional.ofNullable(brandStageName).ifPresent(key -> queryWrapper.eq("brandStageName", key));
        Optional.ofNullable(jobDegree).ifPresent(key -> queryWrapper.eq("jobDegree", key));
        Page<Job> jobList = jobMapper.selectPage(page,queryWrapper);


        PageInfo<Job> pageInfo = new PageInfo<>();
        pageInfo.setJobList(jobList.getRecords());
        pageInfo.setTotal(jobList.getTotal());
        pageInfo.setPageNum(jobList.getCurrent());
        pageInfo.setPageSize(jobList.getSize());
        pageInfo.setPages(jobList.getPages());



        return pageInfo;
    }
    @Override
    public void pointLog(String securityId){
        UpdateWrapper<Job> updateWrapper = new UpdateWrapper<>();
        updateWrapper.setSql("pointCount = pointCount + 1");
        updateWrapper.eq("securityId", securityId);
        jobMapper.update(null,updateWrapper);
    }
    @Override
    public List<Job> getHotJob(){
        QueryWrapper<Job> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("pointCount")
                .ne("pointCount",0)
                .last("LIMIT 20");
        List<Job> hotJobLIst=jobMapper.selectList(queryWrapper);
        return hotJobLIst;
    }

    @Override
    public List<Job> getRecJob(List<Integer> jobIdList){
        List<Job> jobList = new ArrayList<>();
        for (Integer jobId:jobIdList) {

            jobList.add(jobMapper.selectById(jobId));
        }
        return jobList;
    };

    @Override
    public Integer getIdBySecurityId(String securityId){
        QueryWrapper<Job> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("securityId",securityId);
        Job job = jobMapper.selectOne(queryWrapper);
        if(job==null){
            return -1;
        }
        return job.getJobId();
    }

}
