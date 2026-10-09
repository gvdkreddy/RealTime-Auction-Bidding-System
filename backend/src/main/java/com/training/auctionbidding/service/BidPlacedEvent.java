package com.training.auctionbidding.service;

import com.training.auctionbidding.dto.BidResponse;

public record BidPlacedEvent(
        Long auctionId,
        BidResponse response
) {
}