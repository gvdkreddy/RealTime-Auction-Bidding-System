package com.training.auctionbidding.service;

import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuctionStatusEventListener {

    private final SimpMessagingTemplate messagingTemplate;

    @EventListener
    public void handleAuctionStatusChange(
            AuctionStatusEvent event
    ) {

        messagingTemplate.convertAndSend(
                "/topic/auction/" + event.auctionId() + "/status",
                event
        );
    }
}