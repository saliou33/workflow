package com.innov.workflow.app.dto.auth;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class LoginRequest {
    @NotBlank(message = "champ login vide")
    private String username;

    @NotBlank(message = "champ mot de pass vide")
    private String password;
}
