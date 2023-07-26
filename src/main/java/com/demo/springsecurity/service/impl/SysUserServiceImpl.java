package com.demo.springsecurity.service.impl;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.demo.springsecurity.model.ResponseResult;
import com.demo.springsecurity.model.dto.SysUserDTO;
import com.demo.springsecurity.model.entity.SysUser;
import com.demo.springsecurity.service.SysUserService;
import com.demo.springsecurity.utils.JwtUtils;
import com.demo.springsecurity.utils.MD5;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class SysUserServiceImpl implements SysUserService {

    @Autowired(required = false)
    private RedisTemplate<String , String> redisTemplate ;
    @Override
    public ResponseResult<String> login(SysUser user) {



        //登录成功
        //生成token字符串，使用jwt工具类
        String token = JwtUtils.getJwtToken(String.valueOf(user.getId()), user.getPassword());
        // 存Token到redis
        redisTemplate.boundValueOps(user.getName()).set(token);
        return ResponseResult.success(token);
    }
//    @Override
//    public ResponseResult<Map<String ,Object>> login_de(SysUser user) {
//        // 创建Authentication对象
//        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(user.getName() , user.getPassword()) ;
//        // 调用AuthenticationManager的authenticate方法进行认证
//        Authentication authentication = authenticationManager.authenticate(authenticationToken);
////        if(authentication == null) {
////            throw new MyException(401,"用户名或密码错误");
////        }
//        log.info("------------------------------");
//        log.info(authentication.getPrincipal().toString());
//        // 将用户的数据存储到Redis中
//        SysUserDTO loginUser = (SysUserDTO) authentication.getPrincipal() ;
//        Object principal = authentication.getPrincipal();
//        String jsonStr = JSONUtil.toJsonStr(principal);
//        log.info("authentication.getPrincipal():  {}" ,jsonStr);
//        String userId = String.valueOf(loginUser.getSysUser().getId());
//
//        // 生成JWT令牌并进行返回
//        String token = JwtUtils.getJwtToken(userId,user.getName());
//        redisTemplate.boundValueOps("login_user:" + userId).set(JSONUtil.toJsonStr(loginUser));
//
//        // 构建返回数据
//        Map<String , Object> result = new HashMap<>();
//        result.put("token" , token) ;
//        return ResponseResult.success(result);
//    }
    @Override
    public ResponseResult<Object> logout() {
        // 获取登录的用户信息
        SysUserDTO loginUser = (SysUserDTO) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long userId = loginUser.getSysUser().getId();
        // 删除Redis中的用户数据
        redisTemplate.delete("login_user:" + userId) ;
        // 返回
        return ResponseResult.success(null) ;
    }
}
