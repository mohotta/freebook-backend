package org.freebook.backend.user;

import lombok.RequiredArgsConstructor;
import org.freebook.backend.configs.JwtService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserRepository userRepository;
    private  final JwtService jwtService;

    private Logger log = Logger.getLogger(UserController.class.getName());

    @GetMapping("/current")
    public ResponseEntity getCurrentUser(@RequestHeader("Authorization") String token) {
        log.info("Get current user");
        var userEntity = userRepository.findByEmail(jwtService.extractUsername(token.substring(7)));
        if (userEntity.isEmpty()) {
            log.info("User not found!");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal server error!");
        }
        log.info("Request complete");
        return ResponseEntity.ok(userEntity.get());
    }

    @GetMapping("/{userId}")
    public ResponseEntity getUser(@PathVariable UUID userId) {
        log.info("Getting user with id: " + userId);
        var userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) {
            log.info("User not found!");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found!");
        }
        log.info("User found: " + userOptional.get());
        return ResponseEntity.ok(userOptional.get());
    }

    @GetMapping("")
    public ResponseEntity<List<User>> getAllUsers() {
        log.info("Getting all users");
        return ResponseEntity.ok(userRepository.findAll());
    }

    @PutMapping("/{userId}")
    public ResponseEntity updateUser(@PathVariable("userId") UUID userId, @RequestBody UserUpdateRequest request) {
        log.info("Updating user with id: " + userId);
        var user = userRepository.findById(userId);
        if (user.isEmpty()) {
            log.info("User not found!");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found!");
        };
        User oldUser = user.get();
        User newUser = User.builder()
                .id(oldUser.getId())
                .accountId(oldUser.getAccountId())
                .username(oldUser.getUsername())
                .name(request.getName())
                .email(request.getEmail())
                .bio(request.getBio())
                .imgUrl(request.getImgUrl())
                .imgId(request.getImgId())
                .likedPosts(oldUser.getLikedPosts())
                .savedPosts(oldUser.getSavedPosts())
                .build();
        newUser = userRepository.save(newUser);
        log.info("User updated: " + newUser);
        return ResponseEntity.status(HttpStatus.OK).body("User updated!");
    }

}
