package com.capturenow.serviceimpl;

import com.capturenow.module.Customer;
import com.capturenow.module.Photographer;
import com.capturenow.module.RefreshToken;
import com.capturenow.repository.CustomerRepo;
import com.capturenow.repository.PhotographerRepo;
import com.capturenow.repository.RefreshTokenRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepo refreshTokenRepo;
    private final CustomerRepo customerRepo;
    private final PhotographerRepo photographerRepo;

    public RefreshToken createRefreshTokenForCustomer(String email) {
        Customer customer = customerRepo.findByEmail(email);
        RefreshToken refreshToken = RefreshToken.builder()
                .customer(customer)
                .token(UUID.randomUUID().toString())
                .expiryDate(Instant.now().plusMillis(600000)) // 10 minutes for testing
                .build();
        return refreshTokenRepo.save(refreshToken);
    }

    public RefreshToken createRefreshTokenForPhotographer(String email) {
        Photographer photographer = photographerRepo.findByEmail(email);
        RefreshToken refreshToken = RefreshToken.builder()
                .photographer(photographer)
                .token(UUID.randomUUID().toString())
                .expiryDate(Instant.now().plusMillis(600000)) // 10 minutes
                .build();
        return refreshTokenRepo.save(refreshToken);
    }

    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepo.findByToken(token);
    }

    public RefreshToken verifyExpiration(RefreshToken token) {
        if (token.getExpiryDate().compareTo(Instant.now()) < 0) {
            refreshTokenRepo.delete(token);
            throw new RuntimeException(token.getToken() + " Refresh token was expired. Please make a new signin request");
        }
        return token;
    }
}
