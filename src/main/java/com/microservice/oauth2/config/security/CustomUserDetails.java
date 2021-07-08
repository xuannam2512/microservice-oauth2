package com.microservice.oauth2.config.security;

import com.microservice.oauth2.constant.Status;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public class CustomUserDetails extends org.springframework.security.core.userdetails.User {

    private Integer id;

    public CustomUserDetails(Integer id, String username, String password, Status status, Collection<? extends GrantedAuthority> authorities) {
        super(username, password, status.equals(Status.ACTIVE), true, true, true, authorities);
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}
