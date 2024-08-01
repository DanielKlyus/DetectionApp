package com.example.sber_ai.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;
import java.util.Date;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    public String generateToken(Long id) {
        Date expirationDate = Date.from(ZonedDateTime.now().plusMinutes(120).toInstant());

        return JWT.create()
                .withSubject("User details")
                .withClaim("id", id)
                .withExpiresAt(expirationDate)
                .withIssuedAt(new Date())
                .withIssuer("Wild Nature")
                .sign(Algorithm.HMAC512(secret));
    }

    public Long validateToken(String token) {
        JWTVerifier verifier = JWT.require(Algorithm.HMAC512(secret))
                .withSubject("User details")
                .withIssuer("Wild Nature")
                .build();

        DecodedJWT jwt = verifier.verify(token);
        return jwt.getClaim("id").asLong();
    }

}
