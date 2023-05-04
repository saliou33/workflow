package com.innov.workflow.app.dto.user;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class UserRoleDTO {
    @NotNull(message = "userId: the id of the user is required")
    private Long userId;
    @NotNull(message = "roleId: the id of the role is required")
    private Long roleId;
}
