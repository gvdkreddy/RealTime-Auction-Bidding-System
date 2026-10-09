package com.training.auctionbidding;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class AuctionBiddingApplication {

    public static void main(String[] args) {
        SpringApplication.run(
                AuctionBiddingApplication.class,
                args
        );
    }
}