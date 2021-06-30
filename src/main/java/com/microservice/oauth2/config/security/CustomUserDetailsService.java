package com.microservice.oauth2.config.security;

import com.microservice.oauth2.repository.UserRepository;
import com.microservice.oauth2.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserService userService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        try {
            var user = userService.getUserByUsername(username);
            var authorities = Collections.singleton(new SimpleGrantedAuthority(user.getRole().getName()));
            return new CustomUserDetails(user.getId(), user.getUsername(), user.getPassword(), authorities);
        } catch (Exception e) {
            throw new UsernameNotFoundException("User " + username + " was not found in the database");
        }

    }
}
