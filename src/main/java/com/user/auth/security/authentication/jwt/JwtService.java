package com.user.auth.security.authentication.jwt;

import java.util.Map;
import java.util.function.Function;

import org.springframework.security.core.userdetails.UserDetails;

import io.jsonwebtoken.Claims;

public interface JwtService {
	String extractUsername(String token);
    boolean isTokenValid(String token, UserDetails userDetails);
    boolean validateTokenSignature(String token) ;
    String extractAlgorithm(String token) ;
    String generateToken(UserDetails userDetails) ;
    String generateToken(Map<String, Object> claims, UserDetails userDetails) ;
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) ;
}
