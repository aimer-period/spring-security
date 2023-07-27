package com.demo.springsecurity.filter;

import cn.hutool.json.JSONUtil;
import com.demo.springsecurity.model.dto.SysUserDTO;
import com.demo.springsecurity.utils.JwtUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.annotation.Resource;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 认证过滤器
 */
public class JwtAuthenticationTokenFilter extends OncePerRequestFilter {
    @Resource
    private RedisTemplate<String , String> redisTemplate ;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        // 1、从请求头中获取token，如果请求头中不存在token，直接放行即可！由Spring Security的过滤器进行校验！
        String token = request.getHeader("token");
        if(token == null || "".equals(token)) {
            filterChain.doFilter(request , response);
            return ;
        }

        // 2、对token进行解析，取出其中的userId
        String userId = null ;
        try {
            userId= JwtUtils.getMemberIdByJwtToken(request);
        }catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("token非法") ;
        }
        System.out.println(userId);
        // 3、使用userId从redis中查询对应的LoginUser对象
        String loginUserJson = redisTemplate.boundValueOps("login_user:" + userId).get();
        SysUserDTO loginUser = JSONUtil.toBean(loginUserJson, SysUserDTO.class);
        if(loginUser != null) {
            // 4、然后将查询到的LoginUser对象的相关信息封装到UsernamePasswordAuthenticationToken对象中，然后将该对象存储到Security的上下文对象中
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(loginUser, null , null) ;
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        }

        // 5、放行
        filterChain.doFilter(request , response);
    }
}
