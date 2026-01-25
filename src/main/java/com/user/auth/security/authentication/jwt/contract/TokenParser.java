package com.user.auth.security.authentication.jwt.contract;

import java.util.function.Function;

import io.jsonwebtoken.Claims;

public interface TokenParser {
	public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) ;
	String extractUsername(String token) ;
}
