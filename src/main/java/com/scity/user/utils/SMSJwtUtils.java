package com.scity.user.utils;

import io.jsonwebtoken.Header;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class SMSJwtUtils {
    private static final String SID_KEY = "";
    private static final String SECRET_KEY = "";

    public static String createJwt() {
        long currentTimeMillis = System.currentTimeMillis();
        long expirationTimeMillis = currentTimeMillis + 60000L;

        Map<String, Object> claims = new HashMap<>();
        claims.put("jti", SID_KEY + "_" + currentTimeMillis);
        claims.put("iss", SID_KEY);
        claims.put("exp", expirationTimeMillis);
        claims.put("rest_api", true);

        return Jwts.builder()
                .setHeaderParam(Header.TYPE, Header.JWT_TYPE)
                .setHeaderParam("cty", "stringee-api;v=1")
                .setClaims(claims)
                .setExpiration(new Date(expirationTimeMillis))
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY.getBytes())
                .compact();
    }
}