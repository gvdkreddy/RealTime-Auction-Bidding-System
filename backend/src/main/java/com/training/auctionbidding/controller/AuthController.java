package com.training.auctionbidding.controller;

import com.training.auctionbidding.dto.LoginRequest;
import com.training.auctionbidding.dto.RegisterRequest;
import com.training.auctionbidding.entity.User;
import com.training.auctionbidding.service.AuthService;
import com.training.auctionbidding.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<?> register(
            @Valid @RequestBody RegisterRequest request) {

        User user = userService.register(
                request.getName(),
                request.getEmail(),
                request.getPassword()
        );

        return ResponseEntity.ok(
                Map.of(
                        "message", "Registration successful",
                        "userId", user.getId()
                )
        );
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(
            @Valid @RequestBody LoginRequest request) {

        String token = authService.login(request);

        return ResponseEntity.ok(
                Map.of(
                        "token", token,
                        "message", "Login successful"
                )
        );
    }
}