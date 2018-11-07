package com.easylinker.proxy.server.app.config.jwt;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class JwtHelper {
    private static final String SECRET = "jwt-token";

    public static String generateToken(Long userId) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("userId", userId);
        return Jwts.builder()
                .setClaims(map)
                .setExpiration(new Date(System.currentTimeMillis() + 60000L))// 1000 hour
                .signWith(SignatureAlgorithm.HS512, SECRET)
                .compact();
    }

    public static Map validateToken(String token) throws IllegalStateException {

        return Jwts.parser()
                .setSigningKey(SECRET)
                .parseClaimsJws(token)
                .getBody();

    }
}