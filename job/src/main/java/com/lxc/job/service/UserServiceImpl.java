package com.lxc.job.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.lxc.job.entity.LoginInfo;
import com.lxc.job.entity.User;
import com.lxc.job.mapper.UserMapper;
import io.swagger.models.auth.In;
import org.omg.PortableInterceptor.INACTIVE;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.jws.soap.SOAPBinding;
import java.util.*;
import java.util.stream.Collectors;


@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private JobServiceImpl jobService;

    @Override
    public LoginInfo login(User user){
        System.out.println(user);
        LoginInfo loginInfo = new LoginInfo();
        Integer userId =user.getUserId();
        if(userMapper.selectById(userId)==null){
            loginInfo.setStatus(false);
            return loginInfo;
        }
        User u = userMapper.selectById(userId);
        String passwd= u.getPasswd();
        if (user.getPasswd().equals(passwd)){
            loginInfo.setStatus(true);
            loginInfo.setUserId(userId);
            loginInfo.setUserName(u.getUserName());
            return loginInfo;
        };
        loginInfo.setStatus(false);
        return loginInfo;
    }

    @Override
    public Integer register(User user){
        int length = 8; // 指定随机数长度
        Integer userId = generateRandomId(length);
        user.setUserId(userId);
        int res = userMapper.insert(user);
        if (res==1){
            return userId;
        }
        return -1;
    }

    @Override
    public List<Integer> getId() {
        QueryWrapper<User> queryWrapper=new QueryWrapper<>();
        queryWrapper.select("userId");
        List<User> userList = userMapper.selectList(queryWrapper);
        List<Integer> idList = new ArrayList<>();
        for (User user:userList) {
            idList.add(user.getUserId());
        }
        return idList;
    }

    @Override
    public List<Integer> getRecId(Integer userId) {
        List<Integer> jobIdList=new ArrayList<>();

        User u = userMapper.selectById(userId);
        String jobIdStr = u.getAlsRecommendation();
        jobIdStr=jobIdStr.substring(1,jobIdStr.length()-1);
        String[] jobArr = jobIdStr.split(",");
        for (String s:jobArr) {
            jobIdList.add(Integer.parseInt(s));
        }

        return jobIdList;
    }
    @Override
    public Integer viseUserInfo(User user){
        UpdateWrapper updateWrapper = new UpdateWrapper();
        updateWrapper.eq("userId",user.getUserId());
        updateWrapper.set("school",user.getSchool());
        updateWrapper.set("major",user.getMajor());
        updateWrapper.set("degree",user.getDegree());
        updateWrapper.set("hometown",user.getHometown());
        updateWrapper.set("skills", user.getSkills());
        updateWrapper.set("desiredJob",user.getDesiredJob());
        updateWrapper.set("salary", user.getSalary());
        updateWrapper.set("desiredCity",user.getDesiredCity());
        updateWrapper.set("hobby", user.getHobby());
        updateWrapper.set("projectExperience",user.getProjectExperience());

        int row = userMapper.update(null,updateWrapper);
        return row;
    };

    @Override
    public Boolean subTextSpark(User user){
        return null;
    };
    @Override
    public Boolean subALSSpark(){
        return null;
    };

    @Override
    public Boolean addPreference(Integer userId,String securityId){
        List<Integer> jobIdList = new ArrayList<>();
        Integer jobId = jobService.getIdBySecurityId(securityId);
        UpdateWrapper<User> userUpdateWrapper = new UpdateWrapper<>();
        if (jobId==-1){
            return false;
        }
        User u = userMapper.selectById(userId);
        String jobIdStr =  u.getPreference();

        if (jobIdStr==null){
            jobIdList.add(jobId);

            userUpdateWrapper.eq("userId",userId).set("preference",jobIdList.toString());
            userMapper.update(null,userUpdateWrapper);
            return true;
        }
        else {
            jobIdList = Arrays.asList(jobIdStr.substring(1,jobIdStr.length()-1).split(",")).stream().map(str ->Integer.parseInt(str)).collect(Collectors.toList());
            jobIdList.add(jobId);
            userUpdateWrapper.eq("userId",userId).set("preference",jobIdList.toString().replace(" ",""));
            userMapper.update(null,userUpdateWrapper);
            return true;
        }
//        System.out.println(u);
    }

    public Integer generateRandomId(int length) {
        List<Integer> idList = getId();
        Random random = new Random();
        Integer userId=null;
        do {
            StringBuilder sb = new StringBuilder();
            // 生成第一位随机数字，不能为0
            char firstChar = (char) (random.nextInt(9) + '1');
            sb.append(firstChar);
            for (int i = 1; i < length; i++) {
                // 生成一个随机数字，并将其转换为字符类型
                char c = (char) (random.nextInt(10) + '0');
                sb.append(c);
            }
            String randomStr = sb.toString();
            userId = Integer.parseInt(randomStr);
        } while (idList.contains(userId));
        return userId;
    }
}
