package com.user.auth.services.impl;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.user.auth.entities.AccessToken;
import com.user.auth.entities.Session;
import com.user.auth.exceptions.auth.InvalidTokenIdentifierException;
import com.user.auth.repositories.AccessTokenRepository;
import com.user.auth.services.AccessTokenService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AccessTokenServiceImpl implements AccessTokenService {
	
	private static final Duration ACCESS_TTL = Duration.ofMinutes(15) ;
	
	private final AccessTokenRepository accessTokenRepository ;
	
	@Override
	@Transactional
	public AccessToken createAccessToken(Session session) {
		
		return accessTokenRepository.save(
		        AccessToken.builder()
		            .jti(UUID.randomUUID())
		            .sid(session.getSid())
		            .subject(session.getUserId().toString())
		            .expiresAt(Instant.now().plus(ACCESS_TTL))
		            .build()
		    ) ;
	}

	@Override
	@Transactional
	public AccessToken createAccessToken() {
		return accessTokenRepository.save(
		        AccessToken.builder()
		            .jti(UUID.randomUUID())
		            .subject("service-auth")
		            .expiresAt(Instant.now().plus(ACCESS_TTL))
		            .build()
		    );
	}
	
	@Override
	@Transactional
	public void revoke(UUID sid) {
		accessTokenRepository.findBySid(sid)
			.ifPresent(at -> {
				at.setRevokedAt(Instant.now()) ;
				accessTokenRepository.save(at) ;
			}) ;
	}
	
	@Override
	@Transactional
	public void revoke(List<UUID> sids) {
		accessTokenRepository.revokeActiveBySids(sids, Instant.now()) ;
	}

	@Override
	public AccessToken getAccessTokenBySession(UUID sid) {
		return accessTokenRepository.findBySidAndRevokedAtIsNull(sid)
				.orElseThrow(() -> new InvalidTokenIdentifierException ("Inavlid Session")) ;
	}

}
