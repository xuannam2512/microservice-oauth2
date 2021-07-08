package com.microservice.oauth2.redis;

import com.microservice.oauth2.domain.RolePermission;
import com.microservice.oauth2.redis.model.RedisRolePermission;
import com.microservice.oauth2.repository.RolePermissionRepository;
import com.microservice.oauth2.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RedisService {

    private final RedisRepository<RedisRolePermission> repository;
    private final RolePermissionRepository rolePermissionRepository;
    private final RoleRepository roleRepository;

    @PostConstruct
    public void init() {
        roleRepository.findAll()
                .forEach(role -> {
                    repository.delete(role.getCode());
                    var redisRolePermission = new RedisRolePermission();
                    redisRolePermission.setRoleCode(role.getCode());
                    redisRolePermission.setRoleName(role.getName());
                    redisRolePermission.setPermissions(rolePermissionRepository.findAllByRole(role).stream().map(RolePermission::getPermission).collect(Collectors.toList()));
                    repository.add(role.getCode(), redisRolePermission);
                });
    }
}
