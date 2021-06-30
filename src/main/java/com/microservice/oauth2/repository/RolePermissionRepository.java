package com.microservice.oauth2.repository;

import com.microservice.oauth2.domain.Role;
import com.microservice.oauth2.domain.RolePermission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RolePermissionRepository extends JpaRepository<RolePermission, Integer> {

    List<RolePermission> findAllByRole(Role role);
}
