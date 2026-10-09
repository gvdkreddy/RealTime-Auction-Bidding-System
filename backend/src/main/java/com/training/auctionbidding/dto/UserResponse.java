package com.training.auctionbidding.dto;

import com.training.auctionbidding.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
public class UserResponse {

    private Long id;
    private String name;
    private String email;
    private Role role;
    private BigDecimal balance;
    private Boolean active;
    private LocalDateTime createdAt;
    
    private BigDecimal reservedBalance;
    private BigDecimal availableBalance;
}