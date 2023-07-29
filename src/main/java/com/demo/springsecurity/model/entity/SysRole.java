package com.demo.springsecurity.model.entity;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@AllArgsConstructor
@NoArgsConstructor
public class SysRole {

  private long id;
  private String roleName;


  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }


  public String getRoleName() {
    return roleName;
  }

  public void setRoleName(String roleName) {
    this.roleName = roleName;
  }

}
