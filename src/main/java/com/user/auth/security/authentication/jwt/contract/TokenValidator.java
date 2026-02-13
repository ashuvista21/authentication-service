package com.user.auth.security.authentication.jwt.contract;

public interface TokenValidator {
	boolean isTokenValid(String token) ;
    boolean validateTokenSignature(String token) ;
    public boolean introspectToken(String token) ;
}
