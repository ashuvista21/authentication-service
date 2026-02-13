package com.user.auth.security.authentication.jwt.adapter;

import java.util.function.Function;

import org.springframework.stereotype.Service;

import com.nimbusds.jwt.JWTClaimsSet;
import com.user.auth.security.authentication.jwt.contract.TokenClaims;
import com.user.auth.security.authentication.jwt.contract.TokenParser;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AsymmetricTokenParserAdapter implements TokenParser {
	
	private final validator.contract.TokenParser delegate ;

	@Override
	public TokenClaims extractClaim(String token) {
		JWTClaimsSet claimsSet = delegate.extractClaim(token, Function.identity()) ;
		return new NimbusClaimsAdapter(claimsSet) ;
	}

	@Override
	public String extractUsername(String token) {
		return delegate.extractUsername(token) ;
	}

}
