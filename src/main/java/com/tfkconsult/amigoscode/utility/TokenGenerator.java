package com.tfkconsult.amigoscode.utility;

import java.util.Date;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.tfkconsult.amigoscode.domain.AppUser;
import com.tfkconsult.amigoscode.domain.Role;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

@Component
public class TokenGenerator {
    private final Algorithm algorithm = Algorithm.HMAC256("secret".getBytes());

    public String getAccessToken(HttpServletRequest request, HttpServletResponse response, User user) {
        String access_token = JWT.create().withSubject(user.getUsername())
                .withExpiresAt(new Date(System.currentTimeMillis() + 10 * 60 * 1000))
                .withIssuer(request.getRequestURL().toString())
                .withClaim("roles",
                        user.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()))
                .sign(algorithm).toString();
        return access_token;
    }

    public String getRefreshAccessToken(HttpServletRequest request, HttpServletResponse response, AppUser user) {
        String access_token = JWT.create().withSubject(user.getUsername())
                .withExpiresAt(new Date(System.currentTimeMillis() + 10 * 60 * 1000))
                .withIssuer(request.getRequestURL().toString())
                .withClaim("roles",
                        user.getRoles().stream().map(Role::getName).collect(Collectors.toList()))
                .sign(algorithm).toString();
        return access_token;
    }

    public String getRefreshToken(HttpServletRequest request, HttpServletResponse response, User user) {
        String refresh_token = JWT.create().withSubject(user.getUsername())
                .withExpiresAt(new Date(System.currentTimeMillis() + 30 * 60 * 1000))
                .withIssuer(request.getRequestURL().toString()).sign(algorithm).toString();

        return refresh_token;
    }

    public String[] getVerifiedRoles(String token) {
        JWTVerifier verifier = JWT.require(algorithm).build();
        DecodedJWT decodedJWT = verifier.verify(token);
        String[] roles = decodedJWT.getClaim("roles").asArray(String.class);
        return roles;
    }

    public String getVerifiedUsername(String token) {
        JWTVerifier verifier = JWT.require(algorithm).build();
        DecodedJWT decodedJWT = verifier.verify(token);
        String username = decodedJWT.getSubject();
        return username;
    }
}
