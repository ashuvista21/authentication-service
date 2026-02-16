package com.user.auth.services.impl;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.user.auth.entities.RefreshToken;
import com.user.auth.entities.Session;
import com.user.auth.exceptions.auth.InvalidTokenException;
import com.user.auth.exceptions.auth.InvalidTokenIdentifierException;
import com.user.auth.repositories.RefreshTokenRepository;
import com.user.auth.services.RefreshTokenService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RefreshTokenServiceImpl implements RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository ;

    private static final Duration REFRESH_TTL = Duration.ofDays(30) ;
    
	@Override
	@Transactional
	public RefreshToken rotateWithRefreshToken(RefreshToken refreshToken) {
		Instant now = Instant.now();

        // Reuse attack detection
        if (refreshToken.getRevokedAt() != null || refreshToken.getExpiresAt().isBefore(now)) {
        	/*
        	 * In serious systems, reuse detection should also:
        	 * Revoke entire session
        	 * Revoke all tokens of session
        	 * Force re-login
        	 * Right now you only throw exception.
        	 * For now acceptable — just keep in mind.
        	 */
            throw new InvalidTokenIdentifierException("Refresh token reuse detected") ;
        }
        
        refreshToken.revoke() ;
        refreshTokenRepository.save(refreshToken) ;
        
        RefreshToken newToken = RefreshToken.builder()
        		.sid(refreshToken.getSid())
        		.build() ;
        
        newToken.rotate(REFRESH_TTL) ;
        return refreshTokenRepository.save(newToken) ;

	}
	
	@Override
	@Transactional
	public RefreshToken rotateWithSession(Session session) {
		return refreshTokenRepository
		        .findByRefreshTokenAndRevokedAtIsNull(session.getSid())
		        .map(existingRt -> {
		        	RefreshToken newToken = RefreshToken.builder()
		        		    .sid(existingRt.getSid())
		        		    .refreshToken(UUID.randomUUID())
		        		    .issuedAt(Instant.now())
		        		    .expiresAt(Instant.now().plus(REFRESH_TTL))
		        		    .build() ;
		        	// Not revoking previous refresh token
		        	// check this
		            return refreshTokenRepository.save(newToken) ;
		        })
		        .orElseGet(() -> {
		            // 🆕 New session → create refresh token
		            RefreshToken rt = RefreshToken.builder()
		                .sid(session.getSid())
		                .refreshToken(UUID.randomUUID())
		                .expiresAt(Instant.now().plus(REFRESH_TTL))
		                .build();

		            return refreshTokenRepository.save(rt) ;
		        }) ;
	}

	@Override
	public RefreshToken validate(UUID refreshTokenValue) {

	    RefreshToken refreshToken = refreshTokenRepository
	            .findByRefreshToken(refreshTokenValue)
	            .orElseThrow(() ->
	                new InvalidTokenException("Invalid refresh token")
	            ) ;

	    if (refreshToken.getRevokedAt() != null) {
	        throw new InvalidTokenException("Refresh token revoked") ;
	    }

	    if (refreshToken.getExpiresAt().isBefore(Instant.now())) {
	        throw new InvalidTokenException("Refresh token expired") ;
	    }

	    return refreshToken ;
	}
	
	@Override
	@Transactional(propagation = Propagation.MANDATORY)
	public void revoke(UUID sid) {
		refreshTokenRepository.findBySidAndRevokedAtIsNull(sid)
			.ifPresent(rt -> {
				rt.setRevokedAt(Instant.now()) ;
				//dirty checking updates automatically
				//refreshTokenRepository.save(rt) ;
			}) ;
	}
	
	@Override
	@Transactional
	public void revoke(List<UUID> sids) {
		refreshTokenRepository.revokeBySid(sids, Instant.now()) ;
	}
}

