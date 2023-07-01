package com.innov.workflow.app.controller.auth;

import com.innov.workflow.app.dto.auth.LoginRequest;
import com.innov.workflow.app.dto.auth.SignupRequest;
import com.innov.workflow.app.service.AuthCookieService;
import com.innov.workflow.app.service.AuthService;
import com.innov.workflow.core.domain.ApiResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@RestController
@RequestMapping("/api/auth")
@AllArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final AuthCookieService authCookieService;

    @PostMapping("/login")
    public ResponseEntity authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        return authCookieService.login(loginRequest);
    }

    @PostMapping("/signup")
    public ResponseEntity registerUser(@Valid @RequestBody SignupRequest signUpRequest) {

        authService.signup(signUpRequest);
        return ApiResponse.success("compte créé avec succès");
    }

    @GetMapping("/accountVerification/{token}")
    public ResponseEntity verifyAccount(@PathVariable String token) {
        authService.verifyAccount(token);
        return ApiResponse.success("votre compte est activité");
    }

    @PostMapping("/refresh/token")
    public ResponseEntity refreshToken(HttpServletRequest request) {
        return authCookieService.refreshToken(request);
    }

    @GetMapping("/logout")
    public ResponseEntity logoutUser() {
        return authCookieService.logout();
    }


}
