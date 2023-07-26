package com.demo.springsecurity.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.demo.springsecurity.model.entity.SysRole;
import com.demo.springsecurity.model.entity.SysUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SysUserMapper extends BaseMapper<SysUser> {
    @Select("select * from sys_role where id in (select role_id from sys_user_role where user_id = #{id})")
    List<SysRole> getUserRole(Long id);
}
