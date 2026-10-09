package com.training.auctionbidding.dto;

import com.training.auctionbidding.entity.AuctionStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
public class AuctionResponse {

    private Long id;
    private String title;
    private String description;
    private BigDecimal startingPrice;
    private BigDecimal currentPrice;
    private BigDecimal minimumBidIncrement;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private AuctionStatus status;
    private UserResponse seller;
    private UserResponse winner;
    private LocalDateTime createdAt;
}