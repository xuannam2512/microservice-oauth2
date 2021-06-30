package com.microservice.oauth2.controller;

import com.microservice.oauth2.domain.bean.UserDto;
import com.microservice.oauth2.service.RefreshTokenService;
import com.microservice.oauth2.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final UserService userService;
    private final RefreshTokenService refreshTokenService;

    @PostMapping("/register")
    public ResponseEntity<UserDto> register(@RequestBody UserDto userDto) {
        return ResponseEntity.ok(userService.create(userDto));
    }

    @PutMapping("user/{userId}")
    public ResponseEntity<UserDto> update(@PathVariable Integer userId, @RequestBody UserDto userDto) {
        return ResponseEntity.ok(userService.update(userId, userDto));
    }

    @GetMapping("user/{userId}")
    public ResponseEntity<UserDto> get(@PathVariable Integer userId) {
        return ResponseEntity.ok(userService.getById(userId));
    }

    @DeleteMapping("refreshToken/{token}")
    public ResponseEntity<Void> deleteRefreshToken(@PathVariable String token) {
        refreshTokenService.deleteByToken(token);
        return ResponseEntity.ok().build();
    }
}
