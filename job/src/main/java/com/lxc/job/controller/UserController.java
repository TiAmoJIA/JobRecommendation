package com.lxc.job.controller;

import com.lxc.job.entity.Job;
import com.lxc.job.entity.LoginInfo;
import com.lxc.job.entity.User;
import com.lxc.job.entity.UserLog;
import com.lxc.job.service.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@CrossOrigin    //允许跨域
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserServiceImpl userService;

    @PostMapping("/login")
    public LoginInfo login(@RequestBody User user) {
        LoginInfo loginInfo = userService.login(user);
        return loginInfo;
    }

    @PostMapping("/register")
    public Integer register(@RequestBody User user) {

        Integer status=userService.register(user);
        if (status!=-1){
            return status;
        }
        return -1;
    }
    @PostMapping("/userInfo")
    public boolean reviseUserInfo(@RequestBody User user){
        int row =  userService.viseUserInfo(user);
        if (row>0)
            return true;

        return false;
    }
    @PostMapping("/preUp")
    public Boolean addPreference(@RequestBody UserLog userLog){
        User user =  userLog.getUser();
        Job job = userLog.getJob();
        Integer userId = user.getUserId();
        String securityId = job.getSecurityId();
        userService.addPreference(userId,securityId);

        return true;
    }
}
