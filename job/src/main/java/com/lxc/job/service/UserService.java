package com.lxc.job.service;

import com.lxc.job.entity.LoginInfo;
import com.lxc.job.entity.User;

import java.util.List;

public interface UserService {
    LoginInfo login(User user);
    Integer register(User user);
    List<Integer> getId();
    List<Integer> getRecId(Integer userId);
    Integer viseUserInfo(User user);
    Boolean subTextSpark(User user);
    Boolean subALSSpark();
    Boolean addPreference(Integer userId,String securityId);
}
