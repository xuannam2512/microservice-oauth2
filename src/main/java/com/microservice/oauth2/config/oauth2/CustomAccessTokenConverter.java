package com.microservice.oauth2.config.oauth2;

import com.microservice.oauth2.config.security.CustomUserDetails;
import com.microservice.oauth2.service.RefreshTokenService;
import com.microservice.oauth2.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;

import java.util.LinkedHashMap;
import java.util.Map;

public class CustomAccessTokenConverter extends JwtAccessTokenConverter {

    @Autowired
    private RefreshTokenService refreshTokenService;

    @Autowired
    private UserService userService;

    @Override
    public OAuth2AccessToken enhance(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {
        var userDetails = (CustomUserDetails) authentication.getPrincipal();
        var refreshToken = accessToken.getRefreshToken();

        var customAccessToken = new DefaultOAuth2AccessToken(accessToken);
        customAccessToken.setAdditionalInformation(buildClaims(accessToken, userDetails));
        customAccessToken = (DefaultOAuth2AccessToken) super.enhance(customAccessToken, authentication);

        if (refreshTokenService.existToken(refreshToken.getValue())) {
            customAccessToken.setRefreshToken(refreshToken);
        }

        return customAccessToken;
    }

    public Map<String, Object> decode(String token) {
        return super.decode(token);
    }

    private Map<String, Object> buildClaims(OAuth2AccessToken accessToken, CustomUserDetails userDetails) {
        Map<String, Object> info = new LinkedHashMap<>(accessToken.getAdditionalInformation());
        if (userDetails.getId() != null)
            info.put("id", userDetails.getId());
        if (userDetails.getUsername() != null)
            info.put("userName", userDetails.getUsername());

        var permissions = userService.getPermissionByUserId(userDetails.getId());
        info.put("permissions", permissions);

        return info;
    }

}
