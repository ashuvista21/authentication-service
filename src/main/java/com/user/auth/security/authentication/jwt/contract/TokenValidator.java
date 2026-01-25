package com.user.auth.security.authentication.jwt.contract;

import org.springframework.security.core.userdetails.UserDetails;

public interface TokenValidator {
	boolean isTokenValid(String token, UserDetails userDetails) ;
    boolean validateTokenSignature(String token) ;
}
