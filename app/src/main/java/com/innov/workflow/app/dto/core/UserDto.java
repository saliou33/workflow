package com.innov.workflow.app.dto.core;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.innov.workflow.core.domain.entity.Group;
import com.innov.workflow.core.domain.entity.Organization;
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
    private String password;
    private String tel;
    private String avatar;
    private List<SysRole> roles;
    private Organization organization;
    private List<Group> groups;
}
