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

        // Process LIVE auctions that have expired.
        List<Auction> expiredLive =
                auctionRepository.findByStatusAndEndTimeLessThanEqual(
                        AuctionStatus.LIVE, now);

        for (Auction auction : expiredLive) {
            endAuctionSafely(auction.getId());
        }

        // Process UPCOMING auctions whose start time has arrived.
        List<Auction> due =
                auctionRepository.findByStatusAndStartTimeLessThanEqual(
                        AuctionStatus.UPCOMING, now);

        for (Auction auction : due) {
            if (!now.isBefore(auction.getEndTime())) {
                // The auction window has completely elapsed.
                // Do not activate it or accept bids.
                // Settlement support for this legacy state is needed
                // before automatically ending it safely.
                log.warn(
                        "Auction {} is UPCOMING but its end time passed; "
                                + "skipping activation",
                        auction.getId());
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
    }

    private void endAuctionSafely(Long auctionId) {
        try {
            auctionService.endAuction(auctionId);

            eventPublisher.publishEvent(
                    new AuctionStatusEvent(
                            auctionId, AuctionStatus.ENDED));

            log.info("Ended auction {}", auctionId);
        } catch (Exception e) {
            log.error("Failed to end auction {}", auctionId, e);
        }
    }
}
