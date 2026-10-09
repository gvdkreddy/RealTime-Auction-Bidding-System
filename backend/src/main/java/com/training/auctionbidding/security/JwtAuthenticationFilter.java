
package com.training.auctionbidding.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        // Allow CORS preflight requests
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            filterChain.doFilter(request, response);
            return;
        }

        String authHeader = request.getHeader("Authorization");

        // Continue if no JWT is provided
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            System.out.println("JWT header missing for: " + request.getRequestURI());
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7).trim();

        // Debug messages: never print the actual token
        System.out.println("JWT header received: " + !token.isBlank());

        try {
            boolean valid = !token.isBlank() && jwtService.isTokenValid(token);

            System.out.println("JWT valid: " + valid);

            if (!valid) {
                SecurityContextHolder.clearContext();
                filterChain.doFilter(request, response);
                return;
            }

            String username = jwtService.extractUsername(token);

            System.out.println("JWT username: " + username);

            if (username == null || username.isBlank()) {
                SecurityContextHolder.clearContext();
                filterChain.doFilter(request, response);
                return;
            }

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            username,
                            null,
                            List.of(new SimpleGrantedAuthority("ROLE_USER"))
                    );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            System.out.println("Authentication set for: " + username);

        } catch (io.jsonwebtoken.JwtException | IllegalArgumentException exception) {
            SecurityContextHolder.clearContext();

            System.out.println(
                    "JWT validation failed: " + exception.getClass().getSimpleName()
            );
        }

        filterChain.doFilter(request, response);
    }
}
