package com.demo.springsecurity.security;


import cn.hutool.json.JSONConfig;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.demo.springsecurity.model.R;
import com.demo.springsecurity.model.ResultCode;
import com.demo.springsecurity.model.ResultCodeEnum;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * 未授权的统一处理方式
 */
public class UnauthorizedEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {
        //设置客户端的响应的内容类型
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(401);
        //获取输出流
        ServletOutputStream outputStream = response.getOutputStream();
        //消除循环引用
        // 创建 JSONConfig 对象，并设置禁用循环引用检测
        JSONConfig config = JSONConfig.create().setIgnoreNullValue(true);
        String result = JSONUtil.toJsonStr(R.error().code(ResultCode.AUTHENTICATION_ERROR).message("用户认证失败，请重新登录"), config);
        outputStream.write(result.getBytes(StandardCharsets.UTF_8));
        outputStream.flush();
        outputStream.close();
    }
}
