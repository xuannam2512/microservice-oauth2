package com.microservice.oauth2.repository;

import com.microservice.oauth2.domain.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Integer> {

    void deleteByToken(String token);

    Optional<RefreshToken> findFirstByToken(String token);

    boolean existsByToken(String token);
}
