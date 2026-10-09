package com.training.auctionbidding.service;

import com.training.auctionbidding.websocket.AuctionWebSocketService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class BidEventListener {

    private final AuctionWebSocketService webSocketService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleBidPlaced(BidPlacedEvent event) {

        webSocketService.broadcastBid(
                event.auctionId(),
                event.response()
        );
    }
}