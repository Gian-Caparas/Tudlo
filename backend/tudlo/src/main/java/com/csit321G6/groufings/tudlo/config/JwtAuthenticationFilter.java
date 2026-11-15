package com.csit321G6.groufings.tudlo.config;

import com.csit321G6.groufings.tudlo.common.JwtUtil;
import com.csit321G6.groufings.tudlo.common.UserProfileLookup;
import com.csit321G6.groufings.tudlo.entity.User;
import com.csit321G6.groufings.tudlo.entity.UserStatus;
import com.csit321G6.groufings.tudlo.entity.UserType;
import com.csit321G6.groufings.tudlo.repo.UserRepository;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final UserProfileLookup userProfileLookup;

    public JwtAuthenticationFilter(
            JwtUtil jwtUtil,
            UserRepository userRepository,
            UserProfileLookup userProfileLookup) {
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
        this.userProfileLookup = userProfileLookup;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || authHeader.length() < 7 || !authHeader.regionMatches(true, 0, "Bearer ", 0, 7)) {
            filterChain.doFilter(request, response);
            return;
        }

        Claims claims;
        try {
            claims = jwtUtil.parseToken(authHeader.substring(7).trim());
        } catch (RuntimeException invalidToken) {
            filterChain.doFilter(request, response);
            return;
        }

        Long userId;
        Long tokenProfileId;
        Long tokenExpiryMinutes;
        Long declaredLifetimeMillis;
        String email;
        String tokenRole;
        try {
            userId = Long.valueOf(claims.getSubject());
            email = claims.get("email", String.class);
            tokenRole = claims.get("role", String.class);
            tokenProfileId = asLong(claims.get("profileId"));
            tokenExpiryMinutes = asLong(claims.get("expiryMinutes"));
            declaredLifetimeMillis = tokenExpiryMinutes == null
                    ? null
                    : Math.multiplyExact(Math.multiplyExact(tokenExpiryMinutes, 60L), 1000L);
        } catch (RuntimeException invalidClaims) {
            filterChain.doFilter(request, response);
            return;
        }

        if (userId <= 0 || email == null || tokenRole == null || tokenExpiryMinutes == null
                || tokenExpiryMinutes <= 0 || claims.getIssuedAt() == null || claims.getExpiration() == null
                || claims.getIssuedAt().after(new java.util.Date())
                || !claims.getExpiration().after(claims.getIssuedAt())
                || claims.getExpiration().getTime() - claims.getIssuedAt().getTime()
                        != declaredLifetimeMillis) {
            filterChain.doFilter(request, response);
            return;
        }

        Optional<User> activeUser = userRepository.findById(userId)
                .filter(user -> user.getStatus() == UserStatus.ACTIVE)
                .filter(user -> user.getEmail().equalsIgnoreCase(email))
                .filter(user -> user.getUserType().name().equals(tokenRole));
        if (activeUser.isPresent()) {
            User user = activeUser.get();
            Long currentProfileId = userProfileLookup.findProfileId(userId, user.getUserType()).orElse(null);
            if (Objects.equals(tokenProfileId, currentProfileId)) {
                AuthenticatedUser principal = new AuthenticatedUser(userId, user.getEmail(), user.getUserType(),
                        currentProfileId);
                var authorities = List.of(
                        new SimpleGrantedAuthority("ROLE_" + user.getUserType().name()));
                var authToken = new UsernamePasswordAuthenticationToken(principal, null, authorities);
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        filterChain.doFilter(request, response);
    }

    private Long asLong(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Number number) {
            return new BigDecimal(number.toString()).longValueExact();
        }
        if (value instanceof String string) {
            return Long.valueOf(string);
        }
        throw new IllegalArgumentException("Invalid numeric JWT claim");
    }
}