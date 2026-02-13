package com.user.auth.security.authentication.jwt.adapter;

import java.util.Date;

import com.nimbusds.jwt.JWTClaimsSet;
import com.user.auth.security.authentication.jwt.contract.TokenClaims;

public class NimbusClaimsAdapter implements TokenClaims {
	
	private final JWTClaimsSet claims ;
	
	public NimbusClaimsAdapter(JWTClaimsSet claims) {
        this.claims = claims;
    }

	@Override
	public String getSubject() {
		return claims.getSubject() ;
	}

	@Override
	public Date getExpiration() {
		return claims.getExpirationTime() ;
	}

	@Override
	public String getIssuer() {
		return claims.getIssuer() ;
	}

	@Override
	public Object get(String key) {
		return claims.getClaim(key) ;
	}

}
