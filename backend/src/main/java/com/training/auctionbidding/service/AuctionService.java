package com.training.auctionbidding.service;

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
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuctionService {

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

        LocalDateTime now = LocalDateTime.now();

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
                .orElseThrow(() ->
                        new AuctionNotFoundException(id)
                );
    }

    // Start auction
    @Transactional
    public Auction startAuction(Long id) {

        Auction auction = getById(id);

        if (auction.getStatus() == AuctionStatus.CANCELLED) {
            throw new IllegalStateException(
                    "Cancelled auction cannot be started"
            );
        }

        auction.setStatus(AuctionStatus.LIVE);

        return auctionRepository.save(auction);
    }

    // End auction and settle payment
    @Transactional
    public Auction endAuction(Long id) {

        Auction auction = auctionRepository.findByIdForUpdate(id)
                .orElseThrow(() ->
                        new AuctionNotFoundException(id)
                );

        if (auction.getStatus() == AuctionStatus.CANCELLED) {
            throw new IllegalStateException(
                    "Cancelled auction cannot be ended"
            );
        }

        // Prevent settling the same auction twice
        if (auction.getStatus() == AuctionStatus.ENDED) {
            return auction;
        }

        // Find the highest bid
        Bid highestBid = bidRepository
                .findTopByAuctionIdOrderByAmountDesc(id)
                .orElse(null);

        if (highestBid != null) {

            // Lock the winner's wallet
            User winner = userRepository.findByIdForUpdate(
                    highestBid.getBidder().getId()
            ).orElseThrow(() ->
                    new IllegalStateException(
                            "Winner not found"
                    )
            );

            // Lock the seller's wallet
            User seller = userRepository.findByIdForUpdate(
                    auction.getSeller().getId()
            ).orElseThrow(() ->
                    new IllegalStateException(
                            "Seller not found"
                    )
            );

            BigDecimal winningAmount = highestBid.getAmount();

            // Safety check:
            // winner must have enough money reserved
            if (winner.getReservedBalance()
                    .compareTo(winningAmount) < 0) {

                throw new IllegalStateException(
                        "Winner does not have enough reserved balance"
                );
            }

            // Remove winning amount from winner's total balance
            winner.setBalance(
                    winner.getBalance()
                            .subtract(winningAmount)
            );

            // Release the reservation because
            // the reserved money is now being paid
            winner.setReservedBalance(
                    winner.getReservedBalance()
                            .subtract(winningAmount)
            );

            // Transfer winning amount to seller
            seller.setBalance(
                    seller.getBalance()
                            .add(winningAmount)
            );

            // Set auction winner
            auction.setWinner(winner);
        }

        // Mark auction as ended
        auction.setStatus(AuctionStatus.ENDED);

        return auctionRepository.save(auction);
    }

    // Convert Auction entity to AuctionResponse DTO
    public AuctionResponse toResponse(Auction auction) {

        UserResponse seller = auction.getSeller() == null
                ? null
                : userService.toResponse(
                auction.getSeller()
        );

        UserResponse winner = auction.getWinner() == null
                ? null
                : userService.toResponse(
                auction.getWinner()
        );

        return AuctionResponse.builder()
                .id(auction.getId())
                .title(auction.getTitle())
                .description(auction.getDescription())
                .startingPrice(auction.getStartingPrice())
                .currentPrice(auction.getCurrentPrice())
                .minimumBidIncrement(
                        auction.getMinimumBidIncrement()
                )
                .startTime(auction.getStartTime())
                .endTime(auction.getEndTime())
                .status(auction.getStatus())
                .seller(seller)
                .winner(winner)
                .createdAt(auction.getCreatedAt())
                .build();
    }
}