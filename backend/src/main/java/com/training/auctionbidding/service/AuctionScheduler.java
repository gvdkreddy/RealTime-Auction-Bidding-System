package com.training.auctionbidding.service;

import com.training.auctionbidding.entity.Auction;
import com.training.auctionbidding.entity.AuctionStatus;
import com.training.auctionbidding.repository.AuctionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class AuctionScheduler {

    private final ApplicationEventPublisher eventPublisher;
    private final AuctionRepository auctionRepository;
    private final AuctionService auctionService;

    @Scheduled(fixedDelay = 5000)
    public void processAuctions() {

        LocalDateTime now = LocalDateTime.now();

        // Start UPCOMING auctions whose start time has arrived
        List<Auction> upcomingAuctions =
                auctionRepository
                        .findByStatusAndStartTimeLessThanEqual(
                                AuctionStatus.UPCOMING,
                                now
                        );

        for (Auction auction : upcomingAuctions) {

            try {

                auctionService.startAuction(auction.getId());

                eventPublisher.publishEvent(
                        new AuctionStatusEvent(
                                auction.getId(),
                                AuctionStatus.LIVE
                        )
                );

                System.out.println(
                        "Automatically started auction: "
                                + auction.getId()
                );

            } catch (Exception e) {

                System.err.println(
                        "Failed to start auction "
                                + auction.getId()
                                + ": "
                                + e.getMessage()
                );
            }
        }

        // End LIVE auctions whose end time has passed
        List<Auction> expiredAuctions =
                auctionRepository
                        .findByStatusAndEndTimeLessThanEqual(
                                AuctionStatus.LIVE,
                                now
                        );

        for (Auction auction : expiredAuctions) {

            try {

                auctionService.endAuction(auction.getId());

                eventPublisher.publishEvent(
                        new AuctionStatusEvent(
                                auction.getId(),
                                AuctionStatus.ENDED
                        )
                );

                System.out.println(
                        "Automatically ended auction: "
                                + auction.getId()
                );

            } catch (Exception e) {

                System.err.println(
                        "Failed to end auction "
                                + auction.getId()
                                + ": "
                                + e.getMessage()
                );
            }
        }
    }
}