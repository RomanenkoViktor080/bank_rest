package com.example.bankcards.util.crypto;

import lombok.RequiredArgsConstructor;
import org.apache.commons.codec.binary.Hex;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import java.nio.charset.StandardCharsets;

@RequiredArgsConstructor
@Component
public class Sha256Impl implements Sha256 {
    @Qualifier("hmacSha256")
    private final Mac mac;

    @Override
    public String encrypt(String string) {
        try {
            byte[] hash = mac.doFinal(string.replaceAll("\\s+", "").getBytes(StandardCharsets.UTF_8));
            return Hex.encodeHexString(hash);
        } catch (Exception e) {
            throw new RuntimeException("HMAC error", e);
        }
    }
}
