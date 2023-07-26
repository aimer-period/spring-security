package com.demo.springsecurity.service;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.demo.springsecurity.mapper.SysUserMapper;
import com.demo.springsecurity.model.dto.SysUserDTO;
import com.demo.springsecurity.model.entity.SysRole;
import com.demo.springsecurity.model.entity.SysUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
@Slf4j
public class UserDetailServiceImpl implements UserDetailsService {
    @Resource
    SysUserMapper sysUserMapper;
    @Autowired
    PasswordEncoder passwordEncoder;

    @Override
    public SysUserDTO loadUserByUsername(String username) throws UsernameNotFoundException {
        String password = passwordEncoder.encode("123");
        log.info("登录，用户名：{}, 密码：{}", username,password);

        //通过用户名查询用户信息
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getName, username);
        SysUser user = sysUserMapper.selectOne(wrapper);
        log.info("--------------登录用户信息："+ user.toString());
        // 判断是否存在该用户
        if (ObjectUtil.isEmpty(user)){
            throw new UsernameNotFoundException("用户不存在");
        }
        List<GrantedAuthority> admin = AuthorityUtils.commaSeparatedStringToAuthorityList("admin");

        //获取权限信息
        List<SysRole> userRole = sysUserMapper.getUserRole(user.getId());
        log.info(userRole.toString());
        return new SysUserDTO(user, userRole);
    }
}
