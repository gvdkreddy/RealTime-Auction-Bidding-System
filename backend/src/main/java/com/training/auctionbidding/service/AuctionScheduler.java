
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

        log.info("Auction scheduler running. Server time: {}", now);

        List<Auction> upcoming =
                auctionRepository.findByStatusAndStartTimeLessThanEqual(
                        AuctionStatus.UPCOMING, now);

        log.info("Due upcoming auctions found: {}", upcoming.size());

        for (Auction auction : upcoming) {
            try {
                // Don't start auctions whose end time has already passed.
                if (!now.isBefore(auction.getEndTime())) {
                    log.warn(
                            "Auction {} is already past end time; skipping activation",
                            auction.getId());
                    continue;
                }

                log.info("Attempting to start auction {}", auction.getId());

                Auction started =
                        auctionService.startAuction(auction.getId());

                log.info(
                        "Auction {} start result: {}",
                        started.getId(), started.getStatus());

                eventPublisher.publishEvent(
                        new AuctionStatusEvent(
                                started.getId(), started.getStatus()));
            } catch (Exception e) {
                log.error(
                        "Failed to start auction {}",
                        auction.getId(), e);
            }
        }

        List<Auction> expired =
                auctionRepository.findByStatusAndEndTimeLessThanEqual(
                        AuctionStatus.LIVE, now);

        for (Auction auction : expired) {
            try {
                log.info("Attempting to end auction {}", auction.getId());

                Auction ended =
                        auctionService.endAuction(auction.getId());

                eventPublisher.publishEvent(
                        new AuctionStatusEvent(
                                ended.getId(), ended.getStatus()));

                log.info(
                        "Auction {} end result: {}",
                        ended.getId(), ended.getStatus());
            } catch (Exception e) {
                log.error(
                        "Failed to end auction {}",
                        auction.getId(), e);
            }
        }
    }
}
