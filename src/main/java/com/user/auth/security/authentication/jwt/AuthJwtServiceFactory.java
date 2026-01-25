package com.user.auth.security.authentication.jwt;

import org.springframework.stereotype.Component;

import com.user.auth.security.authentication.jwt.contract.JwtAlgorithm;

import validator.contract.TokenParser;
import validator.contract.TokenValidator;
import validator.service.AuthJwtService;

@Component
public class AuthJwtServiceFactory {
	
	private final AuthJwtService authJwtService ;
	
	public AuthJwtServiceFactory(AuthJwtService authJwtService) {
		this.authJwtService = authJwtService ;
	}
	
	public TokenParser getTokenParser(JwtAlgorithm algorithm) {
        return switch (algorithm) {
            case RS256 -> authJwtService ;
            default -> throw new IllegalArgumentException("Unsupported algorithm: " + algorithm) ;
        } ;
    }
    
    public TokenValidator getTokenValidator(JwtAlgorithm algorithm) {
        return switch (algorithm) {
            case RS256 -> authJwtService ;
            default -> throw new IllegalArgumentException("Unsupported algorithm: " + algorithm) ;
        } ;
    }

}
