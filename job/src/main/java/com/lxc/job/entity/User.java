package com.lxc.job.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

@Data
public class User {
    @TableId
    private int userId;

    private String userName;

    private String passwd;

    private String alsRecommendation;
    private String textRecommendation;
    private String degree;
    private String major;
    private String skills;
    private String projectExperience;
    private String hometown;
    private String school;
    private String preference;
    private String hobby;
    private String desiredJob;
    private String desiredCity;
    private String salary;



}
