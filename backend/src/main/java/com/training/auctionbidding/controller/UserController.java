package com.training.auctionbidding.controller;

import com.training.auctionbidding.dto.UserResponse;
import com.training.auctionbidding.entity.User;
import com.training.auctionbidding.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public UserResponse getCurrentUser(Authentication authentication) {
        User user = userService.getByEmail(authentication.getName());
        return userService.toResponse(user);
    }
}