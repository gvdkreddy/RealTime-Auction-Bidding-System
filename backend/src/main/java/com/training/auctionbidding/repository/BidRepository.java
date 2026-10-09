package com.training.auctionbidding.repository;

import com.training.auctionbidding.entity.Bid;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BidRepository extends JpaRepository<Bid, Long> {

    @Query("""
            SELECT b
            FROM Bid b
            JOIN FETCH b.auction
            JOIN FETCH b.bidder
            WHERE b.auction.id = :auctionId
            ORDER BY b.amount DESC
            """)
    List<Bid> findByAuctionIdWithDetails(
            @Param("auctionId") Long auctionId
    );

    List<Bid> findByBidderIdOrderByCreatedAtDesc(Long bidderId);
    Optional<Bid> findTopByAuctionIdOrderByAmountDesc(Long auctionId);
    List<Bid> findByBidderEmailOrderByCreatedAtDesc(String email);
}