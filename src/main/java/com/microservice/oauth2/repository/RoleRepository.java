package com.microservice.oauth2.repository;

import com.microservice.oauth2.domain.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Integer> {
}
