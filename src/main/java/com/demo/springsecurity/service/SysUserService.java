package com.demo.springsecurity.service;

import com.demo.springsecurity.model.ResponseResult;
import com.demo.springsecurity.model.entity.SysUser;

import java.util.Map;

public  interface SysUserService {
    public  ResponseResult<String> login(SysUser user);

    public  ResponseResult<Object> logout();
}
