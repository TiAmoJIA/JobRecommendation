package com.lxc.job.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lxc.job.entity.Job;
import com.lxc.job.entity.PageInfo;
import com.lxc.job.entity.User;
import com.lxc.job.service.JobServiceImpl;
import com.lxc.job.service.UserServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin    //允许跨域
@RestController
@RequestMapping("/jobs")
@Slf4j
public class JobSiftController {

    @Autowired
    private JobServiceImpl jobService;

    @Autowired
    private UserServiceImpl userService;

    @GetMapping(value = "/fliter")
    public PageInfo<Job> showJobsByIndex(@RequestParam(defaultValue = "1") Integer pageNum,
                                     @RequestParam(defaultValue = "10") Integer pageSize,
                                     @RequestParam(required = false) String cityName,              //城市
                                     @RequestParam(required = false) Integer position,      //职位类型
                                     @RequestParam(required = false) Integer industry,      //公司行业
                                     @RequestParam(required = false) String jobExperience,      //工作经验
                                     @RequestParam(required = false) String salaryDesc,         //薪资待遇
                                     @RequestParam(required = false) String brandScaleName,     //公司规模
                                     @RequestParam(required = false) String brandStageName,     //融资阶段
                                     @RequestParam(required = false) String jobDegree           //学历要求


    ){
        Page<Job> page =new Page<>(pageNum,pageSize);
        PageInfo<Job> pageInfo = jobService.getJobByIndex(page,cityName,position,industry,jobExperience,salaryDesc,brandScaleName,brandStageName,jobDegree);
        return pageInfo;
    }
    @PostMapping("/pointCount")
    public String pointLog(@RequestBody Job job){
        log.info(job.toString());
        jobService.pointLog(job.getSecurityId());
        return "SUCCESS";
    }
    @GetMapping("/hot")
    public List<Job> getHotJob(){
        List<Job> hotList = jobService.getHotJob();
        return hotList;
    }

    @GetMapping("/myJobRec")
    public List<Job> getRec(@RequestParam Integer userId){

        List<Job> jobList =  jobService.getRecJob(userService.getRecId(userId));
        System.out.println(jobList);
        return jobList;
    }

}
