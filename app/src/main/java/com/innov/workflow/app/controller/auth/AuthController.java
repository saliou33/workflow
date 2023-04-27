package com.innov.workflow.app.controller.auth;

import com.innov.workflow.app.dto.auth.LoginRequest;
import com.innov.workflow.app.dto.auth.SignupRequest;
import com.innov.workflow.core.domain.ApiResponse;
import com.innov.workflow.core.domain.entity.User;
import com.innov.workflow.core.domain.repository.UserRepository;
import com.innov.workflow.core.exception.ApiException;
import com.innov.workflow.idm.config.jwt.JwtUtils;
import com.innov.workflow.idm.config.service.UserDetailsImpl;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
@AllArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;

    private final UserRepository userRepository;

    private final PasswordEncoder encoder;

    private final JwtUtils jwtUtils;

    @PostMapping("/login")
    public ResponseEntity authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream()
                .map(item -> item.getAuthority())
                .collect(Collectors.toList());

        HashMap<String, Object> responseData = new HashMap<>();
        responseData.put("id", userDetails.getId());
        responseData.put("token", jwt);
        responseData.put("username", userDetails.getUsername());
        responseData.put("email", userDetails.getEmail());
        responseData.put("roles", roles);

        return ApiResponse.success("Utilisateur connecté avec succés", responseData);
    }

    @PostMapping("/signup")
    public ResponseEntity registerUser(@Valid @RequestBody SignupRequest signUpRequest) {

        if (!signUpRequest.getPassword().equals(signUpRequest.getConfirmPassword())) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Les mots des passe ne sont pas identique");
        }

        if (userRepository.existsByUsername(signUpRequest.getUsername())) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "ce username est indisponible");
        }

        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "cet email est indisponible");
        }

        // Create new user's account
        User user = new User();
        user.setName(signUpRequest.getName());
        user.setUsername(signUpRequest.getUsername());
        user.setPassword(encoder.encode(signUpRequest.getPassword()));
        user.setEmail(signUpRequest.getEmail());
        user.setTel(signUpRequest.getTel());

        userRepository.save(user);

        return ApiResponse.success("Votre compte a été créé avec succés");
    }
}
