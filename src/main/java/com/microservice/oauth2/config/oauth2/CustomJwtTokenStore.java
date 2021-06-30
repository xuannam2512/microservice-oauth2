package com.microservice.oauth2.config.oauth2;

import com.microservice.oauth2.service.RefreshTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.common.DefaultOAuth2RefreshToken;
import org.springframework.security.oauth2.common.OAuth2RefreshToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
import org.springframework.stereotype.Component;

@Component
public class CustomJwtTokenStore extends JwtTokenStore {

    @Autowired
    private RefreshTokenService refreshTokenService;

    public CustomJwtTokenStore(JwtAccessTokenConverter jwtTokenEnhancer) {
        super(jwtTokenEnhancer);
    }

    @Override
    public void storeRefreshToken(OAuth2RefreshToken refreshToken, OAuth2Authentication authentication) {
        refreshTokenService.create(refreshToken.getValue(), 2);
    }

    @Override
    public void removeRefreshToken(OAuth2RefreshToken token) {
        refreshTokenService.deleteByToken(token.getValue());
    }

    @Override
    public OAuth2RefreshToken readRefreshToken(String tokenValue) {
        var refreshToken = refreshTokenService.get(tokenValue);
        return new DefaultOAuth2RefreshToken(refreshToken.getToken());
    }
}
