package com.innov.workflow.app.service;

import com.innov.workflow.app.dto.auth.AuthenticationResponse;
import com.innov.workflow.app.dto.auth.LoginRequest;
import com.innov.workflow.app.dto.auth.RefreshTokenRequest;
import com.innov.workflow.app.dto.auth.SignupRequest;
import com.innov.workflow.app.mapper.core.UserMapper;
import com.innov.workflow.app.model.NotificationEmail;
import com.innov.workflow.core.domain.entity.EnumSysRole;
import com.innov.workflow.core.domain.entity.User;
import com.innov.workflow.core.domain.entity.auth.VerificationToken;
import com.innov.workflow.core.domain.repository.auth.VerificationTokenRepository;
import com.innov.workflow.core.exception.ApiException;
import com.innov.workflow.core.service.RefreshTokenService;
import com.innov.workflow.core.service.SysRoleService;
import com.innov.workflow.core.service.UserService;
import com.innov.workflow.idm.config.jwt.JwtUtils;
import com.innov.workflow.idm.config.service.UserDetailsImpl;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Transactional
public class AuthService {

    private final PasswordEncoder passwordEncoder;
    private final UserService userService;
    private final SysRoleService roleService;
    private final MailService mailService;
    private final RefreshTokenService refreshTokenService;
    private final AuthenticationManager authenticationManager;
    private final VerificationTokenRepository verificationTokenRepository;
    private final UserMapper userMapper;
    private final JwtUtils jwtUtils;

    public String hashPassword(String password) {
        return passwordEncoder.encode(password);
    }

    public void signup(SignupRequest signupRequest) {
        if (!signupRequest.getPassword().equals(signupRequest.getConfirmPassword())) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "champs mots de passe inégaux");
        }

        if (userService.existsByUsername(signupRequest.getUsername())) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "ce login existe déjà");
        }

        if (userService.existsByEmail(signupRequest.getEmail())) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "cet email existe déjà");
        }

        // Create new user's account
        User user = new User();
        user.setLastName(signupRequest.getLastname());
        user.setFirstName(signupRequest.getFirstname());
        user.setFullName();
        user.setUsername(signupRequest.getUsername());
        user.setPassword(passwordEncoder.encode(signupRequest.getPassword()));
        user.setEmail(signupRequest.getEmail());
        user.setTel(signupRequest.getTel());
        user.setRoles(Collections.singletonList(roleService.getRoleByName(EnumSysRole.ROLE_USER)));
        user.setEnabled(true);
        User newUser = userService.saveUser(user);
        sendVerificationToken(newUser);
    }

    public void sendVerificationToken(User user) {
        String token = generateVerificationToken(user);

        mailService.sendMail(new NotificationEmail("Activation compte",
                user.getEmail(), "Heureux de vous avoir parmi nous, " +
                "Merci de cliquer sur le lien en dessous pour activer votre compte: " +
                "http://localhost:8083/api/auth/accountVerification/" + token));
    }


    private void fetchUserAndEnable(VerificationToken verificationToken) {
        String username = verificationToken.getUser().getUsername();
        User user = userService.getUserByUsername(username);
        user.setEnabled(true);
        userService.saveUser(user);
    }

    private String generateVerificationToken(User user) {
        String token = UUID.randomUUID().toString();
        VerificationToken verificationToken = new VerificationToken();
        verificationToken.setToken(token);
        verificationToken.setUser(user);

        verificationTokenRepository.save(verificationToken);
        return token;
    }

    public void verifyAccount(String token) {
        Optional<VerificationToken> verificationToken = verificationTokenRepository.findByToken(token);
        fetchUserAndEnable(verificationToken.orElseThrow(() -> new ApiException(HttpStatus.BAD_REQUEST, "Invalid Token")));
    }

    public AuthenticationResponse login(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream()
                .map(item -> item.getAuthority())
                .collect(Collectors.toList());

        User user = userService.getUserByUsername(userDetails.getUsername());

        if (!user.isEnabled()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "votre compte n'est pas activité");
        }

        return generateRefreshToken(jwt, user);

    }

    public AuthenticationResponse refreshToken(RefreshTokenRequest refreshTokenRequest) {
        User user = userService.getUserByUsername(refreshTokenRequest.getUsername());

        refreshTokenService.createRefreshToken(user.getUserId());
        String token = jwtUtils.generateTokenWithUsername(refreshTokenRequest.getUsername());
        return AuthenticationResponse.builder()
                .accessToken(token)
                .refreshToken(refreshTokenRequest.getRefreshToken())
                .expiresAt(Instant.now().plusMillis(jwtUtils.getJwtExpirationMs()))
                .user(userMapper.mapToDto(user))
                .build();
    }

    public AuthenticationResponse generateRefreshToken(String token, User user) {
        return AuthenticationResponse.builder()
                .accessToken(token)
                .refreshToken(refreshTokenService.createRefreshToken(user.getUserId()).getToken())
                .expiresAt(Instant.now().plusMillis(jwtUtils.getJwtExpirationMs()))
                .user(userMapper.mapToDto(user))
                .build();
    }


}