
package com.training.auctionbidding.service;

import com.training.auctionbidding.dto.BidResponse;
import com.training.auctionbidding.entity.Auction;
import com.training.auctionbidding.entity.AuctionStatus;
import com.training.auctionbidding.entity.Bid;
import com.training.auctionbidding.entity.User;
import com.training.auctionbidding.exception.AuctionNotFoundException;
import com.training.auctionbidding.exception.InvalidBidException;
import com.training.auctionbidding.repository.AuctionRepository;
import com.training.auctionbidding.repository.BidRepository;
import com.training.auctionbidding.repository.UserRepository;
import com.training.auctionbidding.websocket.AuctionWebSocketService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BidService {

    private final BidRepository bidRepository;
    private final AuctionRepository auctionRepository;
    private final UserService userService;
    private final ApplicationEventPublisher eventPublisher;
    private final UserRepository userRepository;

    @Transactional
    public BidResponse placeBid(
            Long auctionId,
            BigDecimal amount,
            String bidderEmail
    ) {
        Auction auction = auctionRepository.findByIdForUpdate(auctionId)
                .orElseThrow(() -> new AuctionNotFoundException(auctionId));

        User bidder = userService.getByEmail(bidderEmail);
        LocalDateTime now = LocalDateTime.now();

        if (auction.getStatus() != AuctionStatus.LIVE) {
            throw new InvalidBidException("Auction is not currently live");
        }

        if (now.isBefore(auction.getStartTime())) {
            throw new InvalidBidException("Auction has not started yet");
        }

        if (now.isAfter(auction.getEndTime())) {
            auction.setStatus(AuctionStatus.ENDED);
            throw new InvalidBidException("Auction has already ended");
        }

        if (auction.getSeller().getId().equals(bidder.getId())) {
            throw new InvalidBidException(
                    "Seller cannot bid on their own auction"
            );
        }

        BigDecimal minimumBid = auction.getCurrentPrice()
                .add(auction.getMinimumBidIncrement());

        if (amount.compareTo(minimumBid) < 0) {
            throw new InvalidBidException(
                    "Bid must be at least ₹" + minimumBid
            );
        }

        Bid previousHighestBid =
                bidRepository.findTopByAuctionIdOrderByAmountDesc(auctionId)
                        .orElse(null);

        User lockedBidder =
                userRepository.findByIdForUpdate(bidder.getId())
                        .orElseThrow(() ->
                                new RuntimeException("Bidder not found"));

        if (previousHighestBid != null) {
            Long previousBidderId =
                    previousHighestBid.getBidder().getId();

            if (!previousBidderId.equals(lockedBidder.getId())) {
                User previousBidder =
                        userRepository.findByIdForUpdate(previousBidderId)
                                .orElseThrow(() ->
                                        new RuntimeException(
                                                "Previous bidder not found"
                                        ));

                previousBidder.setReservedBalance(
                        previousBidder.getReservedBalance()
                                .subtract(previousHighestBid.getAmount())
                );

                if (previousBidder.getReservedBalance()
                        .compareTo(BigDecimal.ZERO) < 0) {
                    previousBidder.setReservedBalance(BigDecimal.ZERO);
                }
            } else {
                lockedBidder.setReservedBalance(
                        lockedBidder.getReservedBalance()
                                .subtract(previousHighestBid.getAmount())
                );

                if (lockedBidder.getReservedBalance()
                        .compareTo(BigDecimal.ZERO) < 0) {
                    lockedBidder.setReservedBalance(BigDecimal.ZERO);
                }
            }
        }

        BigDecimal availableBalance =
                lockedBidder.getBalance()
                        .subtract(lockedBidder.getReservedBalance());

        if (availableBalance.compareTo(amount) < 0) {
            throw new InvalidBidException("Insufficient available balance");
        }

        lockedBidder.setReservedBalance(
                lockedBidder.getReservedBalance().add(amount)
        );

        auction.setCurrentPrice(amount);

        Bid bid = Bid.builder()
                .auction(auction)
                .bidder(lockedBidder)
                .amount(amount)
                .build();

        bidRepository.save(bid);
        auctionRepository.save(auction);

        BidResponse response = BidResponse.builder()
                .bidId(bid.getId())
                .auctionId(auction.getId())
                .bidderName(lockedBidder.getName())
                .amount(bid.getAmount())
                .createdAt(bid.getCreatedAt())
                .build();

        eventPublisher.publishEvent(
                new BidPlacedEvent(auction.getId(), response)
        );

        return response;
    }

    public List<Bid> getBidsByAuction(Long auctionId) {
        return bidRepository.findByAuctionIdWithDetails(auctionId);
    }

    @Transactional(readOnly = true)
    public List<BidResponse> getMyBids(String email) {
        return bidRepository
                .findByBidderEmailOrderByCreatedAtDesc(email)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public BidResponse toResponse(Bid bid) {
        return BidResponse.builder()
                .bidId(bid.getId())
                .auctionId(bid.getAuction().getId())
                .bidderName(bid.getBidder().getName())
                .amount(bid.getAmount())
                .createdAt(bid.getCreatedAt())
                .build();
    }
}
