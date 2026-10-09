package com.training.auctionbidding.service;

import com.training.auctionbidding.entity.User;
import com.training.auctionbidding.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.training.auctionbidding.dto.UserResponse;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserResponse toResponse(User user) {

        BigDecimal availableBalance = user.getBalance()
                .subtract(user.getReservedBalance());

        return UserResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole())
                .balance(user.getBalance())
                .reservedBalance(user.getReservedBalance())
                .availableBalance(availableBalance)
                .active(user.getActive())
                .createdAt(user.getCreatedAt())
                .build();
    }

    public User register(
            String name,
            String email,
            String password) {

        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException("Email already registered");
        }

        User user = User.builder()
                .name(name)
                .email(email)
                .password(passwordEncoder.encode(password))
                .build();

        return userRepository.save(user);
    }

    public User getByEmail(String email) {

        return userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));
    }

    public User getById(Long id) {

        return userRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));
    }
    @Transactional
    public User getByEmailForUpdate(String email) {
        return userRepository.findByEmailForUpdate(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}