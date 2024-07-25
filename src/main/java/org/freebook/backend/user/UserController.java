package org.freebook.backend.user;

import lombok.RequiredArgsConstructor;
import org.freebook.backend.configs.JwtService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class UserController {

    private final UserRepository userRepository;
    private  final JwtService jwtService;

    @GetMapping("")
    public ResponseEntity<User> getCurrentUser(@RequestHeader("Authorization") String token) {
        return ResponseEntity.ok(userRepository.findByEmail(jwtService.extractUsername(token.substring(7))));
    }

}
