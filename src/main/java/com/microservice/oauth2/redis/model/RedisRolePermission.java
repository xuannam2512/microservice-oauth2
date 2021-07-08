package com.microservice.oauth2.redis.model;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.microservice.oauth2.domain.Permission;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS)
public class RedisRolePermission implements Serializable {

    private static final long serialVersionUID = 1L;

    private String roleCode;
    private String roleName;
    private List<Permission> permissions;
}
