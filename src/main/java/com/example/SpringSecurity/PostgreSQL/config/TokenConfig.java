package com.example.SpringSecurity.PostgreSQL.config;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.SpringSecurity.PostgreSQL.domain.entity.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Optional;

@Component
public class TokenConfig {

    @Value("${api.security.token.secret}") 
    private String secret ;

    public String generateToken(User user){
        Algorithm algorithm = Algorithm.HMAC256(secret);
        return JWT.create()
                .withClaim("userId" , user.getId())
                .withSubject(user.getEmail())
                .withClaim("role" ,user.getRole().name())
                .withExpiresAt(Instant.now().plusSeconds(600))
                .withIssuedAt(Instant.now())
                .sign(algorithm);
    }

    public Optional<JWTUserData> validateToken(String token) {
        try{
            Algorithm algorithm = Algorithm.HMAC256(secret);
            DecodedJWT decodedJWT = JWT.require(algorithm).build().verify(token);
            return Optional.of(JWTUserData.builder()
                    .userId(decodedJWT.getClaim("userId").asLong())
                    .email(decodedJWT.getSubject())
                    .role(decodedJWT.getClaim("role").asString())
                    .build());
        }
        catch(JWTVerificationException e ){
            return Optional.empty();
        }
    }
}
