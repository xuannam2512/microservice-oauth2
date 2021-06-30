package com.microservice.oauth2.repository;

import com.microservice.oauth2.domain.Permission;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PermissionRepository extends JpaRepository<Permission, Integer> {
}
