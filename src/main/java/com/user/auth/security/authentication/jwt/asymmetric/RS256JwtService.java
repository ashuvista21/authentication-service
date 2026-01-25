package com.user.auth.security.authentication.jwt.asymmetric;

import java.security.PrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.user.auth.dtos.Jwk;
import com.user.auth.dtos.JwkSet;
import com.user.auth.entities.AccessToken;
import com.user.auth.security.authentication.jwt.contract.PublicKeyProvider;
import com.user.auth.security.authentication.jwt.contract.TokenGenerator;
import com.user.auth.security.authentication.jwt.contract.TokenMetadataValidator;
import com.user.auth.security.authentication.jwt.utils.JwtUtils;
import com.user.auth.services.AccessTokenService;
import com.user.auth.services.SessionService;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RS256JwtService implements TokenGenerator, PublicKeyProvider, TokenMetadataValidator {
	
	private final PrivateKey privateKey ;
	private final RSAPublicKey publicKey ;
	
	private static final long EXPIRATION_TIME = 1000 * 60 * 60; // 1 hour
	
	private final SessionService sessionService ;
	private final AccessTokenService accessTokenService ;

	@Override
	public String generateToken(Map<String, Object> headers, UserDetails userDetails) {
		Map<String, Object> claims = new HashMap<>() ;
        claims.put("roles", userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList()) ;
        return generateToken(headers, claims, userDetails) ;
	}

	@Override
	public String generateToken(Map<String, Object> headers, Map<String, Object> claims, UserDetails userDetails) {
		claims.putIfAbsent("roles", userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList()) ;
        return Jwts.builder()
        		.setHeader(headers)
                .setClaims(claims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(privateKey, SignatureAlgorithm.RS256) // ✅ signed with private key
                .compact();
	}
	
	@Override
	public JwkSet getPublicKey() {
		Jwk jwk = JwtUtils.fromRsaPublicKey(publicKey, "RS256", "my-key-id-1") ;
		return new JwkSet(List.of(jwk)) ;
	}

	@Override
	public boolean validateTokenMetadata(String jti, String sid) {
		
		UUID sessionID = UUID.fromString(sid) ;
		
		sessionService.findActiveSession(sessionID) ;
		
		AccessToken accessToken = accessTokenService.getAccessTokenBySession(sessionID) ;
		
		return accessToken.getJti().toString().equals(jti) ;
	}
}
