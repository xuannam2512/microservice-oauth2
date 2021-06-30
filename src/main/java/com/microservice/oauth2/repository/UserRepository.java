package com.microservice.oauth2.repository;

import com.microservice.oauth2.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {

    Optional<User> findFirstByUsername(String username);
}
