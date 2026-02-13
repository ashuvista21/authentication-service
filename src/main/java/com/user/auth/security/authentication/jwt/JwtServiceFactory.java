package com.user.auth.security.authentication.jwt;

import org.springframework.stereotype.Component;

import com.user.auth.security.authentication.jwt.adapter.AsymmetricTokenParserAdapter;
import com.user.auth.security.authentication.jwt.adapter.AsymmetricTokenValidatorAdapter;
import com.user.auth.security.authentication.jwt.asymmetric.RS256JwtService;
import com.user.auth.security.authentication.jwt.contract.JwtAlgorithm;
import com.user.auth.security.authentication.jwt.contract.PublicKeyProvider;
import com.user.auth.security.authentication.jwt.contract.TokenGenerator;
import com.user.auth.security.authentication.jwt.contract.TokenParser;
import com.user.auth.security.authentication.jwt.contract.TokenValidator;
import com.user.auth.security.authentication.jwt.symmetric.HS256JwtService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtServiceFactory {
    
    private final HS256JwtService hs256JwtService ;
    private final RS256JwtService rs256JwtService ;
    private final AsymmetricTokenParserAdapter parserAdapter ;
    private final AsymmetricTokenValidatorAdapter validatorAdapter ;

    public TokenGenerator getTokenGenerator(JwtAlgorithm algorithm) {
        return switch (algorithm) {
            case HS256 -> hs256JwtService ;
            case RS256 -> rs256JwtService ;
            default -> throw new IllegalArgumentException("Unsupported algorithm: " + algorithm) ;
        } ;
    }
    
    public TokenParser getTokenParser(JwtAlgorithm algorithm) {
        return switch (algorithm) {
            case HS256 -> hs256JwtService ;
            case RS256 -> parserAdapter ;
            default -> throw new IllegalArgumentException("Unsupported algorithm: " + algorithm) ;
        } ;
    }
    
    public TokenValidator getTokenValidator(JwtAlgorithm algorithm) {
        return switch (algorithm) {
            case HS256 -> hs256JwtService ;
            case RS256 -> validatorAdapter ;
            default -> throw new IllegalArgumentException("Unsupported algorithm: " + algorithm) ;
        } ;
    }
    
    public PublicKeyProvider getPublicKeyProvider(JwtAlgorithm algorithm) {
        return switch (algorithm) {
            case RS256 -> rs256JwtService ;
            default -> throw new IllegalArgumentException("Unsupported algorithm: " + algorithm) ;
        } ;
    }
}

