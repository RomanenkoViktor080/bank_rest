package com.example.bankcards.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

@Slf4j
@Configuration
public class CryptoConfig {
    @Value("${hmac.key}")
    private String base64Key;

    @Bean
    public Mac hmacSha256() {
        try {
            byte[] keyBytes = Base64.getDecoder().decode(base64Key);
            SecretKeySpec secretKey = new SecretKeySpec(keyBytes, "HmacSHA256");
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(secretKey);
            return mac;
        } catch (Exception e) {
            throw new RuntimeException("Init error", e);
        }
    }
}
