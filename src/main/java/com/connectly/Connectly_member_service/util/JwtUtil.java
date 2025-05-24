package com.connectly.Connectly_member_service.util;

import com.connectly.Connectly_member_service.model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class JwtUtil {

    @Value("27db492b2b786841d4b899deb459d6fb9df0c247df59d648ab7c6d0054accb8b")
    private String SECRET_KEY;

    @Value("86400000")
    private long EXPIRATION_TIME;


    public String generateToken(UserDetails userDetails, String apiToken, List<String> roles, String email) {
        Map<String, Object> claims = new HashMap<>();

        claims.put("roles", roles);
        claims.put("apiToken",apiToken);
        claims.put("userEmail",email);
        return createToken(claims, userDetails.getUsername());
    }

    public String generateAutoLoginKey() {
        return UUID.randomUUID().toString();
    }

    private String createToken(Map<String, Object> claims, String subject) {
        String token =
                Jwts.builder()
                        .setClaims(claims)
                        .setSubject(subject)
                        .setIssuedAt(new Date())
                        .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                        .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                        .compact();
        System.out.println("Token: " + token);
        return token;
    }

    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .setSigningKey(SECRET_KEY)
                .parseClaimsJws(token)
                .getBody();
    }

    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }
}
