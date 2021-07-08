package com.microservice.oauth2.config.filter;

import com.microservice.oauth2.config.oauth2.CustomAccessTokenConverter;
import com.microservice.oauth2.config.security.CustomUserDetails;
import com.microservice.oauth2.constant.Status;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
@Log4j2
@RequiredArgsConstructor
@Order(1)
public class JwtFilter extends GenericFilter {

    private final CustomAccessTokenConverter customAccessTokenConverter;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        var httpRequest = (HttpServletRequest) servletRequest;

        if (SecurityContextHolder.getContext().getAuthentication() == null) {
            try {
                var authorization = httpRequest.getHeader("Authorization");
                if (authorization != null && authorization.toLowerCase().startsWith("bearer")) {
                    var jwtToken = authorization.substring(7);
                    var claims = customAccessTokenConverter.decode(jwtToken);
                    if (claims.get("id") != null && claims.get("username") != null && claims.get("authorities") != null) {
                        var id = Integer.valueOf(claims.get("id").toString());
                        var username = claims.get("username").toString();
                        var status = claims.get("status").toString();
                        List<SimpleGrantedAuthority> authorities = Stream.of(claims.get("authorities"))
                                .map(authority -> new SimpleGrantedAuthority(String.valueOf(authority)))
                                .collect(Collectors.toList());
                        var principal = new CustomUserDetails(id, username, null, Status.valueOf(status), authorities);
                        var authentication = new UsernamePasswordAuthenticationToken(principal, null, authorities);
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                    }
                }
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        }

        filterChain.doFilter(servletRequest, servletResponse);
    }
}
