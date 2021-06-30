package com.microservice.oauth2.service;

import com.microservice.oauth2.domain.RefreshToken;
import com.microservice.oauth2.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository repository;

    @Transactional
    public void create(String tokenValue, Integer userId) {
        var refreshToken = RefreshToken.builder()
                .token(tokenValue)
                .userId(userId)
                .build();
        repository.save(refreshToken);
    }

    @Transactional(readOnly = true)
    public RefreshToken get(String token) {
        return repository.findFirstByToken(token).get();
    }

    @Transactional
    public void deleteByToken(String tokenValue) {
        repository.deleteByToken(tokenValue);
    }

    @Transactional(readOnly = true)
    public boolean existToken(String tokenValue) {
        return repository.existsByToken(tokenValue);
    }
}
