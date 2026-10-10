
package com.training.auctionbidding.service;

import com.training.auctionbidding.entity.Auction;
import com.training.auctionbidding.entity.AuctionStatus;
import com.training.auctionbidding.repository.AuctionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class AuctionScheduler {

    private final ApplicationEventPublisher eventPublisher;
    private final AuctionRepository auctionRepository;
    private final AuctionService auctionService;

    @Scheduled(fixedDelay = 5000)
    public void processAuctions() {
        LocalDateTime now = LocalDateTime.now();

        // Start only auctions that are within their scheduled window.
        List<Auction> upcomingAuctions =
                auctionRepository.findByStatusAndStartTimeLessThanEqual(
                        AuctionStatus.UPCOMING, now);

        for (Auction auction : upcomingAuctions) {
            if (!now.isBefore(auction.getEndTime())) {
                // Already expired: do not start it.
                // Handle separately after adding overdue settlement support.
                continue;
            }

            try {
                auctionService.startAuction(auction.getId());

                eventPublisher.publishEvent(
                        new AuctionStatusEvent(
                                auction.getId(), AuctionStatus.LIVE));

                log.info("Started auction {}", auction.getId());
            } catch (Exception e) {
                log.error("Failed to start auction {}", auction.getId(), e);
            }
        }

        // End LIVE auctions whose end time has arrived.
        List<Auction> expiredAuctions =
                auctionRepository.findByStatusAndEndTimeLessThanEqual(
                        AuctionStatus.LIVE, now);

        for (Auction auction : expiredAuctions) {
            try {
                auctionService.endAuction(auction.getId());

                eventPublisher.publishEvent(
                        new AuctionStatusEvent(
                                auction.getId(), AuctionStatus.ENDED));

                log.info("Ended auction {}", auction.getId());
            } catch (Exception e) {
                log.error("Failed to end auction {}", auction.getId(), e);
            }
        }
    }
}
