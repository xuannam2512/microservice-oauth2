package com.microservice.oauth2.utils;

import lombok.Data;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.security.*;
import java.security.cert.CertificateException;

@Log4j2
@Data
@Component
public class RSAEncrypt {

    @Value("${config.jwt.secret-key}")
    private String secretKey;

    @Value("${config.jwt.alias}")
    private String alias;

    private KeyStore keyStore;

    @PostConstruct
    public void init() {
        try {
            keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            var resourceAsStream = getClass().getResourceAsStream("/.keystore");
            keyStore.load(resourceAsStream, secretKey.toCharArray());
        } catch (KeyStoreException | CertificateException | NoSuchAlgorithmException | IOException e) {
            log.error("Error when generate keystore", e.getMessage());
        }
    }

    private PrivateKey getPrivateKey() {
        try {
            return (PrivateKey) keyStore.getKey(alias, secretKey.toCharArray());
        } catch (UnrecoverableKeyException | NoSuchAlgorithmException | KeyStoreException e) {
            log.error("Error when get private key", e.getMessage());
            return null;
        }
    }

    private PublicKey getPublicKey() {
        try {
            return keyStore.getCertificate(alias).getPublicKey();
        } catch (KeyStoreException e) {
            log.error("Error when get public key", e.getMessage());
            return null;
        }
    }

    public KeyPair getKeyPair() {
        return new KeyPair(getPublicKey(), getPrivateKey());
    }
}
