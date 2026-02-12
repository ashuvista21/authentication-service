package com.user.auth.security.authentication.jwt.contract.adapter;

import org.springframework.stereotype.Service;

import com.user.auth.security.authentication.jwt.contract.TokenValidator;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AsymmetricTokenValidatorAdapter implements TokenValidator {
	
	private final validator.contract.TokenValidator delegate ;

	@Override
	public boolean isTokenValid(String token) {
		return delegate.isTokenValid(token) ;
	}

	@Override
	public boolean validateTokenSignature(String token) {
		return delegate.validateTokenSignature(token) ;
	}
}
