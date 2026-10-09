
package com.training.auctionbidding.controller;

import com.training.auctionbidding.dto.BidRequest;
import com.training.auctionbidding.dto.BidResponse;
import com.training.auctionbidding.service.BidService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class BidController {

    private final BidService bidService;

    @PostMapping("/api/auctions/{auctionId}/bids")
    public BidResponse placeBid(
            @PathVariable Long auctionId,
            @Valid @RequestBody BidRequest request,
            Authentication authentication
    ) {
        return bidService.placeBid(
                auctionId,
                request.getAmount(),
                authentication.getName()
        );
    }

    @GetMapping("/api/auctions/{auctionId}/bids")
    public List<BidResponse> getBids(
            @PathVariable Long auctionId
    ) {
        return bidService.getBidsByAuction(auctionId)
                .stream()
                .map(bidService::toResponse)
                .toList();
    }

    @GetMapping("/api/bids/my")
    public List<BidResponse> getMyBids(
            Authentication authentication
    ) {
        return bidService.getMyBids(authentication.getName());
    }
}
