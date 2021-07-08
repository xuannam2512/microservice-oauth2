package com.microservice.oauth2.config;

import com.microservice.oauth2.config.oauth2.CustomAccessTokenConverter;
import com.microservice.oauth2.utils.RSAEncrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;

@Configuration
public class BeanCreation {

    @Autowired
    private RSAEncrypt rsaEncrypt;

    @Bean
    public CustomAccessTokenConverter customAccessTokenConverter() {
        var converter = new CustomAccessTokenConverter();
        converter.setKeyPair(rsaEncrypt.getKeyPair());
        return converter;
    }

    @Bean
    public PasswordEncoder encoder() {
        return new BCryptPasswordEncoder();
    }
}
