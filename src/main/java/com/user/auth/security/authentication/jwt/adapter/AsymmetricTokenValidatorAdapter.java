package com.user.auth.security.authentication.jwt.adapter;

import org.springframework.stereotype.Service;

import com.user.auth.config.AuthConfigProperties;
import com.user.auth.security.authentication.jwt.contract.TokenValidator;
import com.user.auth.security.authentication.service.TokenIntrospectionService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AsymmetricTokenValidatorAdapter implements TokenValidator {
	
	private final validator.contract.TokenValidator delegate ;
    private final TokenIntrospectionService introspectionService ;
    private final AuthConfigProperties authConfigProperties ;

	@Override
	public boolean isTokenValid(String token) {
		return delegate.isTokenValid(token) ;
	}

	@Override
	public boolean validateTokenSignature(String token) {
		return delegate.validateTokenSignature(token) ;
	}

	@Override
	public boolean introspectToken(String token) {
		boolean flag = authConfigProperties.useExternalIntrospection() ;
		boolean valid = isTokenValid(token) ;
		
		if(flag) {
			return valid && delegate.introspectToken(token) ;
		}
		
		return valid && introspectionService.introspect(token) ;
	}
}
