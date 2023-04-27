package com.innov.workflow.app.dto.auth;

import com.innov.workflow.core.domain.entity.Role;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.List;

@Data
public class SignupRequest {

    @NotBlank(message = "le champs nom est vide")
    @Size(message = "le nom doit être compris entre 3 et 100 caractéres")
    private String name;

    @NotBlank(message = "le champs username est vide")
    @Size(min = 3, max = 60, message = "le username doit être compris entre 3 et 60 caractéres")
    private String username;

    @Size(max = 100, message = "le champs email ne doit pas dépasser 100 caractéres")
    @Email(message = "l'email est invalide")
    @NotBlank(message = "le champs email est vide")
    private String email;

    @NotBlank(message = "le champs mot de passe est vide")
    @Size(min = 8, max = 40, message = "le mot de passe doit être compris entre 8 et 40 caractéres")
    private String password;

    @NotBlank(message = "le champs confirmer mot de passe est vide")
    private String confirmPassword;

    @Pattern(regexp = "^\\d{10}$", message = "le numero de téléphone est invalide")
    private String tel;
}
