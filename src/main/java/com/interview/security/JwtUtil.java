package com.interview.security;

import java.util.Date;

import javax.crypto.SecretKey;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

public class JwtUtil {

    private static final SecretKey KEY =
            Keys.hmacShaKeyFor(
                    "ThisIsMySecretKeyForJWTAuthentication123456"
                            .getBytes());

    public static String generateToken(
            String username) {

        return Jwts.builder()
                .subject(username)
                .issuedAt(
                        new Date())
                .expiration(
                        new Date(
                                System.currentTimeMillis()
                                        + 86400000))
                .signWith(KEY)
                .compact();
    }
}