package com.training.auctionbidding.service;

import com.training.auctionbidding.entity.User;
import com.training.auctionbidding.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class WalletService {

    private final UserRepository userRepository;

    @Transactional
    public User deposit(String email, BigDecimal amount) {

        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException(
                    "Deposit amount must be greater than zero"
            );
        }

        User user = userRepository.findByEmailForUpdate(email)
                .orElseThrow(() ->
                        new RuntimeException("User not found")
                );

        BigDecimal currentBalance = user.getBalance();

        if (currentBalance == null) {
            currentBalance = BigDecimal.ZERO;
        }

        user.setBalance(currentBalance.add(amount));

        return userRepository.save(user);
    }
}
