package com.microservice.oauth2.config.filter;

import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Component
@RequiredArgsConstructor
@Order(2)
public class BasicFilter extends GenericFilter {

    private final ClientDetailsService clientDetailsService;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        var httpRequest = (HttpServletRequest) servletRequest;

        if (SecurityContextHolder.getContext().getAuthentication() == null) {
            final var authorization = httpRequest.getHeader("Authorization");
            if (authorization != null && authorization.toLowerCase().startsWith("basic")) {
                var clientDetails = getClientDetails(authorization);
                var principal = new User(clientDetails.getClientId(), clientDetails.getClientSecret(), clientDetails.getAuthorities());
                var authentication = new UsernamePasswordAuthenticationToken(principal, clientDetails.getClientSecret(), clientDetails.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }
        filterChain.doFilter(servletRequest, servletResponse);
    }

    private ClientDetails getClientDetails(String authorization) {
        var base64Credentials = authorization.substring("Basic".length()).trim();
        var credDecoded = Base64.getDecoder().decode(base64Credentials);
        var credentials = new String(credDecoded, StandardCharsets.UTF_8);
        final var values = credentials.split(":", 2);
        return clientDetailsService.loadClientByClientId(values[0]);
    }
}
