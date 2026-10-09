package com.training.auctionbidding.controller;

import com.training.auctionbidding.dto.AuctionRequest;
import com.training.auctionbidding.dto.AuctionResponse;
import com.training.auctionbidding.entity.Auction;
import com.training.auctionbidding.service.AuctionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/auctions")
@RequiredArgsConstructor
public class AuctionController {

    private final AuctionService auctionService;

    @PostMapping
    public ResponseEntity<AuctionResponse> createAuction(
            @Valid @RequestBody AuctionRequest request,
            Authentication authentication) {

        Auction auction = auctionService.createAuction(
                request,
                authentication.getName()
        );

        return ResponseEntity.ok(
                auctionService.toResponse(auction)
        );
    }

    @GetMapping
    public List<AuctionResponse> getAllAuctions() {
        return auctionService.getAllAuctions()
                .stream()
                .map(auctionService::toResponse)
                .toList();
    }

    @GetMapping("/{id}")
    public AuctionResponse getAuction(@PathVariable Long id) {
        return auctionService.toResponse(
                auctionService.getById(id)
        );
    }

    @PutMapping("/{id}/start")
    public ResponseEntity<AuctionResponse> startAuction(
            @PathVariable Long id) {

        Auction auction = auctionService.startAuction(id);

        return ResponseEntity.ok(
                auctionService.toResponse(auction)
        );
    }

    @PutMapping("/{id}/end")
    public ResponseEntity<AuctionResponse> endAuction(
            @PathVariable Long id) {

        Auction auction = auctionService.endAuction(id);

        return ResponseEntity.ok(
                auctionService.toResponse(auction)
        );
    }
}