package com.training.auctionbidding.service;

import org.springframework.scheduling.annotation.Scheduled;
import com.training.auctionbidding.dto.AuctionRequest;
import com.training.auctionbidding.dto.AuctionResponse;
import com.training.auctionbidding.dto.UserResponse;
import com.training.auctionbidding.entity.Auction;
import com.training.auctionbidding.entity.AuctionStatus;
import com.training.auctionbidding.entity.Bid;
import com.training.auctionbidding.entity.User;
import com.training.auctionbidding.exception.AuctionNotFoundException;
import com.training.auctionbidding.repository.AuctionRepository;
import com.training.auctionbidding.repository.BidRepository;
import com.training.auctionbidding.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuctionService {

    private static final ZoneId AUCTION_ZONE =
            ZoneId.of("Asia/Kolkata");

    private final AuctionRepository auctionRepository;
    private final UserService userService;
    private final BidRepository bidRepository;
    private final UserRepository userRepository;

    // Create a new auction
    public Auction createAuction(
            AuctionRequest request,
            String sellerEmail
    ) {
        if (!request.getEndTime().isAfter(request.getStartTime())) {
            throw new IllegalArgumentException(
                    "End time must be after start time"
            );
        }

        User seller = userService.getByEmail(sellerEmail);
        LocalDateTime now = LocalDateTime.now(AUCTION_ZONE);

        AuctionStatus status;

        if (now.isBefore(request.getStartTime())) {
            status = AuctionStatus.UPCOMING;
        } else if (now.isBefore(request.getEndTime())) {
            status = AuctionStatus.LIVE;
        } else {
            status = AuctionStatus.ENDED;
        }

        Auction auction = Auction.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .startingPrice(request.getStartingPrice())
                .currentPrice(request.getStartingPrice())
                .minimumBidIncrement(request.getMinimumBidIncrement())
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .status(status)
                .seller(seller)
                .createdAt(now)
                .build();

        return auctionRepository.save(auction);
    }

    // Get all auctions
    public List<Auction> getAllAuctions() {
        return auctionRepository.findAllWithUsers();
    }

    // Get auction by ID
    public Auction getById(Long id) {
        return auctionRepository.findByIdWithUsers(id)
                .orElseThrow(() -> new AuctionNotFoundException(id));
    }

    // Start auction
    @Transactional
    public Auction startAuction(Long id) {
        Auction auction = auctionRepository.findByIdForUpdate(id)
                .orElseThrow(() -> new AuctionNotFoundException(id));

        if (auction.getStatus() == AuctionStatus.CANCELLED) {
            throw new IllegalStateException(
                    "Cancelled auction cannot be started"
            );
        }

        LocalDateTime now = LocalDateTime.now(AUCTION_ZONE);

        if (now.isBefore(auction.getStartTime())) {
            throw new IllegalStateException(
                    "Auction cannot start before its scheduled start time"
            );
        }

        if (!now.isBefore(auction.getEndTime())) {
            throw new IllegalStateException(
                    "Auction has already reached its end time"
            );
        }

        auction.setStatus(AuctionStatus.LIVE);
        return auctionRepository.save(auction);
    }

    // End auction and settle payment
    @Transactional
    public Auction endAuction(Long id) {
        Auction auction = auctionRepository.findByIdForUpdate(id)
                .orElseThrow(() -> new AuctionNotFoundException(id));

        if (auction.getStatus() == AuctionStatus.CANCELLED) {
            throw new IllegalStateException(
                    "Cancelled auction cannot be ended"
            );
        }

        // Prevent settling the same auction twice
        if (auction.getStatus() == AuctionStatus.ENDED) {
            return auction;
        }

        Bid highestBid = bidRepository
                .findTopByAuctionIdOrderByAmountDesc(id)
                .orElse(null);

        if (highestBid != null) {
            User winner = userRepository.findByIdForUpdate(
                    highestBid.getBidder().getId()
            ).orElseThrow(() ->
                    new IllegalStateException("Winner not found")
            );

            User seller = userRepository.findByIdForUpdate(
                    auction.getSeller().getId()
            ).orElseThrow(() ->
                    new IllegalStateException("Seller not found")
            );

            BigDecimal winningAmount = highestBid.getAmount();

            if (winner.getReservedBalance().compareTo(winningAmount) < 0) {
                throw new IllegalStateException(
                        "Winner does not have enough reserved balance"
                );
            }

            winner.setBalance(
                    winner.getBalance().subtract(winningAmount)
            );

            winner.setReservedBalance(
                    winner.getReservedBalance().subtract(winningAmount)
            );

            seller.setBalance(
                    seller.getBalance().add(winningAmount)
            );

            auction.setWinner(winner);
        }

        auction.setStatus(AuctionStatus.ENDED);
        return auctionRepository.save(auction);
    }

    // Automatically update auction statuses every 10 seconds
    @Scheduled(fixedRate = 10000)
    @Transactional
    public void updateAuctionStatuses() {
        LocalDateTime now = LocalDateTime.now(AUCTION_ZONE);

        List<Auction> auctions = auctionRepository.findAll();

        for (Auction auction : auctions) {
            if (auction.getStatus() == AuctionStatus.CANCELLED
                    || auction.getStatus() == AuctionStatus.ENDED) {
                continue;
            }

            if (!now.isBefore(auction.getEndTime())) {
                endAuction(auction.getId());
            } else if (!now.isBefore(auction.getStartTime())) {
                auction.setStatus(AuctionStatus.LIVE);
                auctionRepository.save(auction);
            } else {
                auction.setStatus(AuctionStatus.UPCOMING);
                auctionRepository.save(auction);
            }
        }
    }

    // Convert Auction entity to AuctionResponse DTO
    public AuctionResponse toResponse(Auction auction) {
        UserResponse seller = auction.getSeller() == null
                ? null
                : userService.toResponse(auction.getSeller());

        UserResponse winner = auction.getWinner() == null
                ? null
                : userService.toResponse(auction.getWinner());

        return AuctionResponse.builder()
                .id(auction.getId())
                .title(auction.getTitle())
                .description(auction.getDescription())
                .startingPrice(auction.getStartingPrice())
                .currentPrice(auction.getCurrentPrice())
                .minimumBidIncrement(auction.getMinimumBidIncrement())
                .startTime(auction.getStartTime())
                .endTime(auction.getEndTime())
                .status(auction.getStatus())
                .seller(seller)
                .winner(winner)
                .createdAt(auction.getCreatedAt())
                .build();
    }
}
