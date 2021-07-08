package com.microservice.oauth2.config.oauth2;

import com.microservice.oauth2.config.security.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.core.userdetails.UserDetailsByNameServiceWrapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.ResourceServerTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationProvider;

import java.util.Collections;

@Configuration
@RequiredArgsConstructor
public class OAuth2Config extends AuthorizationServerConfigurerAdapter {

    @Value("${config.oauth2.clientId}")
    private String clientId;

    @Value("${config.oauth2.clientSecret}")
    private String clientSecret;

    private final CustomAccessTokenConverter customAccessTokenConverter;

    private final PasswordEncoder passwordEncoder;

    @Qualifier("authenticationManagerBean")
    private final AuthenticationManager authenticationManager;

    private final CustomUserDetailsService customUserDetailsService;

    @Override
    public void configure(AuthorizationServerSecurityConfigurer security) {
        security.tokenKeyAccess("permitAll()")
                .tokenKeyAccess("isAuthenticated()")
                .checkTokenAccess("isAuthenticated()")
                .accessDeniedHandler(new MyAccessDeniedHandler())
                .authenticationEntryPoint(new MyAuthenticationEntryPoint());
    }

    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        clients.inMemory().withClient(clientId)
                .secret(passwordEncoder.encode(clientSecret))
                .scopes("read", "write")
                .authorizedGrantTypes("password", "authorization_code", "refresh_token", "implicit")
                .accessTokenValiditySeconds(3600)
                .refreshTokenValiditySeconds(3600);
    }

    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) {
        endpoints.authenticationManager(authenticationManager)
                .tokenServices(tokenServices())
                .tokenStore(tokenStore())
                .accessTokenConverter(customAccessTokenConverter)
                .userDetailsService(customUserDetailsService)
                .exceptionTranslator(new MyWebResponseExceptionTranslator());
    }

    @Bean
    public AuthorizationServerTokenServices tokenServices() {
        var tokenServices = new DefaultTokenServices();
        tokenServices.setTokenStore(tokenStore());
        tokenServices.setTokenEnhancer(customAccessTokenConverter);
        tokenServices.setAuthenticationManager(createPreAuthProvider());
        tokenServices.setReuseRefreshToken(true);
        tokenServices.setSupportRefreshToken(true);
        return tokenServices;
    }

    @Bean
    public ResourceServerTokenServices resourceServerTokenServices() {
        var tokenServices = new DefaultTokenServices();
        tokenServices.setTokenStore(tokenStore());
        tokenServices.setTokenEnhancer(customAccessTokenConverter);
        tokenServices.setAuthenticationManager(createPreAuthProvider());
        tokenServices.setReuseRefreshToken(true);
        tokenServices.setSupportRefreshToken(true);
        return tokenServices;
    }

    private ProviderManager createPreAuthProvider() {
        var provider = new PreAuthenticatedAuthenticationProvider();
        provider.setPreAuthenticatedUserDetailsService(new UserDetailsByNameServiceWrapper<>(customUserDetailsService));
        return new ProviderManager(Collections.singletonList(provider));
    }

    @Bean
    public TokenStore tokenStore() {
        return new CustomJwtTokenStore(customAccessTokenConverter);
    }
}
