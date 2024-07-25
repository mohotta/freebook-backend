package org.freebook.backend.auth;


import lombok.RequiredArgsConstructor;
import org.freebook.backend.configs.JwtService;
import org.freebook.backend.user.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.UUID;
import java.util.logging.Logger;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthUserRepository authUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authManager;
    private final UserRepository userRepository;

    private Logger log = Logger.getLogger(AuthService.class.getName());

    public AuthResponse register(RegisterRequest request) {
        // create auth account
        log.info("Register request received");
        var authUser = AuthUser.builder()
                .id(UUID.randomUUID())
                .name(request.getName())
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(UserRoles.USER)
                .build();
        authUser = authUserRepository.save(authUser);
        var jwtToken = jwtService.generateToken(authUser);
        log.info("Register successful: " + authUser);

        // sign up
        var user = User.builder()
                .id(UUID.randomUUID())
                .name(request.getName())
                .username(request.getUsername())
                .email(request.getEmail())
                .accountId(authUser.getId())
                .bio("")
                .imgUrl("") //todo: adding images
                .imgId("")
                .likedPosts(new ArrayList<>())
                .savedPosts(new ArrayList<>())
                .build();
        userRepository.save(user);

        return new AuthResponse(jwtToken);
    }

    public AuthResponse authenticate(AuthRequest request) {
        log.info("Authenticate request: " + request);
        authManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );
        var user = authUserRepository.findByEmail(request.getEmail())
                .orElseThrow();
        var jwtToken = jwtService.generateToken(user);
        log.info("Authenticate successful");
        return new AuthResponse(jwtToken);
    }

}
