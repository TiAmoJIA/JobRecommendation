package com.lxc.job.entity;


import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Job {
//    jobUrl:https://www.zhipin.com/job_detail/{encryptJobId}.html?lid={lid}&securityId={securityId}&sessionId=
    @TableId
    private Integer jobId;

    private String securityId;
    private String bossAvatar;
    private String encryptBossId;
    private String bossName;
    private String bossTitle;
    private String encryptJobId;
    private String jobName;
    private String lid;
    private String salaryDesc;
    private String jobLabels;
    private String skills;
    private String jobExperience;
    private String jobDegree;
    private String cityName;
    private String areaDistrict;
    private String businessDistrict;
    private Integer city;
    private String encryptBrandId;
    private String brandName;
    private String brandLogo;
    private String brandStageName;          //融资状况
    private String brandIndustry;
    private String brandScaleName;
    private String welfareList;
    private Integer industry;
    private String positionIndex;
    private long pointCount;

}
