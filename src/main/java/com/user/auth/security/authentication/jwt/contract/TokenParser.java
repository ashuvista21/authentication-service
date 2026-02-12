package com.user.auth.security.authentication.jwt.contract;

public interface TokenParser {
	public TokenClaims extractClaim(String token) ;
	String extractUsername(String token) ;
}
