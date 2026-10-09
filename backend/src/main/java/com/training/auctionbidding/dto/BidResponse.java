package com.training.auctionbidding.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class BidResponse {

    private Long bidId;

    private Long auctionId;

    private String bidderName;

    private BigDecimal amount;

    private LocalDateTime createdAt;
}