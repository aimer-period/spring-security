package com.demo.springsecurity.filter;


import com.alibaba.fastjson.JSON;
import com.demo.springsecurity.exception.CustomerAuthenticationException;
import com.demo.springsecurity.model.R;
import com.demo.springsecurity.model.ResultCode;
import com.demo.springsecurity.model.dto.SysUserDTO;
import com.demo.springsecurity.model.entity.SysUser;
import com.demo.springsecurity.security.TokenManager;
import com.demo.springsecurity.utils.ResponseUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

/**
 * 登录过滤器，继承UsernamePasswordAuthenticationFilter，对用户名密码进行登录校验
 *
 * @author starsea
 * @since 2019-11-08
 */
public class TokenLoginFilter extends UsernamePasswordAuthenticationFilter {

    private AuthenticationManager authenticationManager;
    private TokenManager tokenManager;
    private RedisTemplate redisTemplate;

    public TokenLoginFilter(AuthenticationManager authenticationManager, TokenManager tokenManager, RedisTemplate redisTemplate) {
        this.authenticationManager = authenticationManager;
        this.tokenManager = tokenManager;
        this.redisTemplate = redisTemplate;
        this.setPostOnly(false);
        this.setRequiresAuthenticationRequestMatcher(new AntPathRequestMatcher("/login","POST"));
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest req, HttpServletResponse res) throws AuthenticationException {
        try {
            //TODO 用户类属性更改了就需要修改
            SysUser user = new ObjectMapper().readValue(req.getInputStream(), SysUser.class);
            return authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(user.getName(), user.getPassword(), new ArrayList<>()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 登录成功
     * @param req
     * @param res
     * @param chain
     * @param auth
     * @throws IOException
     * @throws ServletException
     */
    @Override
    protected void successfulAuthentication(HttpServletRequest req, HttpServletResponse res, FilterChain chain,
                                            Authentication auth) throws IOException, ServletException {
        SysUserDTO user = (SysUserDTO) auth.getPrincipal();
        String token = tokenManager.createToken(user.getSysUser().getName());
        redisTemplate.opsForValue().set(user.getSysUser().getName(), user.getAuthorities(),1800000, TimeUnit.SECONDS);
        System.out.println("t1111oken = " + token);
        ResponseUtil.out(res, R.ok().data("token", token));
    }

    /**
     * 登录失败
     * @param request
     * @param response
     * @param exception
     * @throws IOException
     * @throws ServletException
     */
    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                              AuthenticationException exception) throws IOException, ServletException {
//        ResponseUtil.out(response, R.error());

        //设置客户端的响应内容类型
        response.setContentType("application/json");
        response.setCharacterEncoding("utf-8");
        //获取输出流
        ServletOutputStream outputStream = response.getOutputStream();
        String message = null;
        int code = ResultCode.ERROR;
        //判断异常类型
        if (exception instanceof AccountExpiredException) {
            message = "账户过期，登录失败！";
        } else if (exception instanceof BadCredentialsException) {
            message = "用户名或密码错误，登录失败！";
        } else if (exception instanceof CredentialsExpiredException) {
            message = "密码过期，登录失败！";
        } else if (exception instanceof DisabledException) {
            message = "账户被禁用，登录失败！";
        } else if (exception instanceof LockedException) {
            message = "账户被锁，登录失败！";
        } else if (exception instanceof InternalAuthenticationServiceException) {
            message = "账户不存在，登录失败！";
        }else if(exception instanceof CustomerAuthenticationException){
            message = exception.getMessage();
            code = ResultCode.FORBIDDEN;
        } else {
            message = "登录失败！";
        }
        //将错误信息转换成JSON
        String result = JSON.toJSONString(R.error().code(code).message(message));
        outputStream.write(result.getBytes(StandardCharsets.UTF_8));
        outputStream.flush();
        outputStream.close();
    }
}
