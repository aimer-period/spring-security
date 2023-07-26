package com.demo.springsecurity.model.dto;

import com.demo.springsecurity.model.entity.SysRole;
import com.demo.springsecurity.model.entity.SysUser;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@NoArgsConstructor
@Data
//(1)必须继承UserDetails
public class SysUserDTO implements UserDetails {
    //(2)把用户信息封装成实体类，比较容易管理和操作，比如说新增一些字段，只需在实体类里面加上即可
    private SysUser sysUser;

    private List<SysRole> sysRoles;

    //(3)权限信息，这里需要注意的是要禁止序列化，不然存储到缓存中会有问题
    @JsonIgnore
    private List<GrantedAuthority> authorities;

    public SysUserDTO(SysUser sysUser, List<SysRole> roles) {
        this.sysUser = sysUser;
        this.sysRoles = roles;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (!CollectionUtils.isEmpty(authorities)) {
            return authorities;
        }
        authorities = new ArrayList<>();
        if (sysRoles != null && sysRoles.size() > 0) {
            for (SysRole role : sysRoles) {
                String perms = role.getRoleName();
                if (StringUtils.hasLength(perms)) {
                    SimpleGrantedAuthority simpleGrantedAuthority = new SimpleGrantedAuthority(role.getRoleName());
                    authorities.add(simpleGrantedAuthority);
                }
            }
        }

        return authorities;
    }

    @Override
    public String getPassword() {
        return this.sysUser.getPassword();
    }

    @Override
    public String getUsername() {
        return this.sysUser.getName();
    }

    @Override
    public boolean isAccountNonExpired() {
        //账号是否过期，因为用户表里面没有这个字段，因此默认账号不过期，下面几个方法同理
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
