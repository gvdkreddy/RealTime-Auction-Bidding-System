package com.training.auctionbidding.controller;

import com.training.auctionbidding.dto.DepositRequest;
import com.training.auctionbidding.dto.UserResponse;
import com.training.auctionbidding.service.UserService;
import com.training.auctionbidding.service.WalletService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/wallet")
@RequiredArgsConstructor
public class WalletController {

    private final WalletService walletService;
    private final UserService userService;

    @PostMapping("/deposit")
    public UserResponse deposit(
            @Valid @RequestBody DepositRequest request,
            Authentication authentication
    ) {
        walletService.deposit(
                authentication.getName(),
                request.amount()
        );

        return userService.toResponse(
                userService.getByEmail(authentication.getName())
        );
    }
}
