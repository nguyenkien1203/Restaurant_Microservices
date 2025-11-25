package com.restaurant.authservice.utils;

import io.jsonwebtoken.Jwts;

import java.security.KeyPair;
import java.util.Base64;

public class KeyGenerator {
    public static void main(String[] args) {
        // 1. Generate a 2048-bit RSA KeyPair
        KeyPair encPair = Jwts.SIG.RS256.keyPair().build();
        // Pair 2: For Signing
        KeyPair sigPair = Jwts.SIG.RS256.keyPair().build();

        System.out.println("JWT_ENC_PRIVATE_KEY=" + Base64.getEncoder().encodeToString(encPair.getPrivate().getEncoded()));
        System.out.println("JWT_ENC_PUBLIC_KEY=" + Base64.getEncoder().encodeToString(encPair.getPublic().getEncoded()));

        System.out.println("JWT_SIG_PRIVATE_KEY=" + Base64.getEncoder().encodeToString(sigPair.getPrivate().getEncoded()));
        System.out.println("JWT_SIG_PUBLIC_KEY=" + Base64.getEncoder().encodeToString(sigPair.getPublic().getEncoded()));
    }
}
