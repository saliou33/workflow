package com.innov.workflow.app.dto.auth;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class LoginRequest {
    @NotBlank(message = "le champs username est vide")
    private String username;

    @NotBlank(message = "le champs mot de passe est vide")
    private String password;
}
