package com.training.auctionbidding.websocket;

import com.training.auctionbidding.dto.BidResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuctionWebSocketService {

    private final SimpMessagingTemplate messagingTemplate;

    public void broadcastBid(
            Long auctionId,
            BidResponse response) {

        messagingTemplate.convertAndSend(
                "/topic/auction/" + auctionId,
                response
        );
    }
}