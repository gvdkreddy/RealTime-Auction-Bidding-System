package com.training.auctionbidding.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class AuctionRequest {

    @NotBlank
    private String title;

    private String description;

    @NotNull
    @DecimalMin(value = "0.01")
    private BigDecimal startingPrice;

    @NotNull
    @DecimalMin(value = "0.01")
    private BigDecimal minimumBidIncrement;

    @NotNull
    private LocalDateTime startTime;

    @NotNull
    private LocalDateTime endTime;
}