package com.training.auctionbidding.service;

import com.training.auctionbidding.entity.AuctionStatus;

public record AuctionStatusEvent(
        Long auctionId,
        AuctionStatus status
) {
}