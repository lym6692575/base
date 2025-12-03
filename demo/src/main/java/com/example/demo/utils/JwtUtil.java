package com.example.demo.utils;

import io.jsonwebtoken.*;

import java.util.Date;

public class JwtUtil {

    private static final String SECRET_KEY = "yourSecretKey";  // 请将 "yourSecretKey" 替换为你自己的密钥

    public static String generateToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1800000))  // 设置 token 过期时间
                .signWith(SignatureAlgorithm.HS512, SECRET_KEY)
                .compact();
    }

    public static String validateToken(String token) {
        try {
            return Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token).getBody().getSubject();
        } catch (ExpiredJwtException | UnsupportedJwtException | MalformedJwtException | SignatureException | IllegalArgumentException e) {
            return null;
        }
    }
}