package com.training.auctionbidding.repository;

import com.training.auctionbidding.entity.Auction;
import com.training.auctionbidding.entity.AuctionStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface AuctionRepository extends JpaRepository<Auction, Long> {

    List<Auction> findByStatus(AuctionStatus status);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT a FROM Auction a WHERE a.id = :id")
    Optional<Auction> findByIdForUpdate(@Param("id") Long id);

    // Fetch seller and winner together with the auction
    @Query("""
            SELECT a
            FROM Auction a
            LEFT JOIN FETCH a.seller
            LEFT JOIN FETCH a.winner
            WHERE a.id = :id
            """)
    Optional<Auction> findByIdWithUsers(@Param("id") Long id);

    // Fetch seller and winner for all auctions
    @Query("""
            SELECT a
            FROM Auction a
            LEFT JOIN FETCH a.seller
            LEFT JOIN FETCH a.winner
            """)
    List<Auction> findAllWithUsers();

    List<Auction> findByStatusAndEndTimeLessThanEqual(
            AuctionStatus status,
            LocalDateTime time
    );
    List<Auction> findByStatusAndStartTimeLessThanEqual(
            AuctionStatus status,
            LocalDateTime time
    );
}