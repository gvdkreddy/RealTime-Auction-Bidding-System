package com.training.auctionbidding.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record DepositRequest(

        @NotNull(message = "Deposit amount is required")
        @DecimalMin(value = "1.00", message = "Deposit amount must be at least ₹1")
        @Digits(integer = 12, fraction = 2, message = "Invalid deposit amount")
        BigDecimal amount

) {
}
