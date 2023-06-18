package com.innov.workflow.app.dto.auth;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
public class SignupRequest {

    @NotBlank(message = "le champs prenom est vide")
    @Size(message = "votre nom doit être compris entre 3 et 100 caractères")
    private String firstname;

    @NotBlank(message = "le champs nom est vide")
    @Size(message = "votre nom doit être compris entre 3 et 100 caractères")
    private String lastname;

    @NotBlank(message = "le champs username est vide")
    @Size(min = 4, max = 100, message = "votre username doit être compris entre 4 et 64 caractères")
    private String username;

    @Size(max = 100, message = "le champs email ne doit pas dépasser 100 caractères")
    @Email(message = "votre email est invalide")
    @NotBlank(message = "le champs email est vide")
    private String email;

    @NotBlank(message = "le champs mot de passe est vide")
    @Size(min = 8, max = 64, message = "votre mot de passe doit être compris entre 8 et 64 caractères")
    private String password;

    @NotBlank(message = "le champs confirmer mot de passe est vide")
    private String confirmPassword;

    @Pattern(regexp = "^\\d{10}$", message = "le numéro de téléphone est invalide")
    private String tel;
}
