package com.microservice.oauth2.redis;

import com.microservice.oauth2.domain.RolePermission;
import com.microservice.oauth2.redis.model.RedisRolePermission;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface RedisMapper {

    @Mapping(target = "roleCode", source = "role.code")
    @Mapping(target = "roleName", source = "role.name")
    RedisRolePermission toRedisRolePermission(RolePermission rolePermission);
}
