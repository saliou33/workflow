package com.innov.workflow.app.dto.core;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.innov.workflow.core.domain.entity.SysRole;
import lombok.Data;

import java.util.List;

@Data
public class UserDto extends BaseDto {

    private String id;
    private String username;
    private String lastName;
    private String firstName;
    private String fullName;
    private String email;
    @JsonIgnore
    private String password;
    private String tel;
    private String avatar;
    private List<GroupDto> roles;
    private List<SysRole> sysRoles;
}
