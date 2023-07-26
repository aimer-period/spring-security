package com.demo.springsecurity.controller;

import com.demo.springsecurity.model.ResponseResult;
import com.demo.springsecurity.model.entity.SysUser;
import com.demo.springsecurity.service.SysUserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
public class loginController {
    @Resource
    private SysUserService sysUserService;

    @GetMapping("/test")
    public ResponseResult<Object> login(){

        return ResponseResult.success(new ArrayList<>());
    }
    @GetMapping("/hello")
    public String hello() {
        return "hello,hresh";
    }

    @GetMapping("/hresh")
    public String sayHello() {
        return "hello,world";
    }

    //登录
    @PostMapping("/login")
    public ResponseResult<Object> login(SysUser user) {
        ResponseResult<Map<String, Object>> result = sysUserService.login(user);
        return ResponseResult.success(result.getData());
    }
}
