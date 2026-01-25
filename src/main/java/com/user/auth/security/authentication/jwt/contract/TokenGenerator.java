package com.user.auth.security.authentication.jwt.contract;

import java.util.Map;

import org.springframework.security.core.userdetails.UserDetails;

public interface TokenGenerator {
	String generateToken(Map<String, Object> headers, UserDetails userDetails) ;
    String generateToken(Map<String, Object> headers, Map<String, Object> claims, UserDetails userDetails) ;
}
