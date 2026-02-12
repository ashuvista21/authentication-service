package com.user.auth.security.authentication.jwt.contract.adapter;

import java.util.Date;

import com.user.auth.security.authentication.jwt.contract.TokenClaims;

import io.jsonwebtoken.Claims;

public class JjwtClaimsAdapter implements TokenClaims{
	
	private final Claims claims ;
	
	public JjwtClaimsAdapter(Claims claims) {
        this.claims = claims ;
    }
	
	@Override
	public String getSubject() {
		return claims.getSubject() ;
	}

	@Override
	public Date getExpiration() {
		return claims.getExpiration() ;
	}

	@Override
	public String getIssuer() {
		return claims.getIssuer() ;
	}

	@Override
	public Object get(String key) {
		return claims.get(key) ;
	}

}
