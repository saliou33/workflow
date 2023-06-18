package com.innov.workflow.app.dto.auth;

import com.innov.workflow.app.dto.core.UserDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@AllArgsConstructor
@Builder
public class AuthenticationResponse {
    private UserDto user;
    private String accessToken;
    private String refreshToken;
    private Instant expiresAt;
}
