package com.innov.workflow.app.dto.auth;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
public class SignupRequest {

    @NotBlank(message = "champ prenom vide")
    @Size(message = "champ nom doit être compris entre 3 et 100 caractères")
    private String firstname;

    @NotBlank(message = "champ nom vide")
    @Size(message = "champ nom doit être compris entre 3 et 100 caractères")
    private String lastname;

    @NotBlank(message = "champ login vide")
    @Size(min = 4, max = 100, message = "champ login doit être compris entre 4 et 100 caractères")
    private String username;

    @Size(max = 100, message = "champ email ne doit pas dépasser 100 caractères")
    @Email(message = "champ email invalide")
    @NotBlank(message = "le champ email est vide")
    private String email;

    @NotBlank(message = "champ mot de passe vide")
    @Size(min = 4, max = 64, message = "champ mot de passe doit comprendre entre 4 et 64 caractères")
    private String password;

    @NotBlank(message = "champ confirmer mot de passe vide")
    private String confirmPassword;

    @Pattern(regexp = "^\\d{9}$", message = "champ tel invalide")
    private String tel;
}
