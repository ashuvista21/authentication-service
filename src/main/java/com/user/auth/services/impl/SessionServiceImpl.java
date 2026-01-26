package com.user.auth.services.impl;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.user.auth.entities.Session;
import com.user.auth.exceptions.auth.InvalidTokenIdentifierException;
import com.user.auth.exceptions.auth.MaximumLimitReachedException;
import com.user.auth.repositories.SessionRepository;
import com.user.auth.services.AccessTokenService;
import com.user.auth.services.RefreshTokenService;
import com.user.auth.services.SessionService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SessionServiceImpl implements SessionService {
	
	@Value("${auth.max-active-sessions}")
    private int MAX_ACTIVE_SESSIONS ;
	
	private final SessionRepository sessionRepository ;
	private final AccessTokenService accessTokenService ;
	private final RefreshTokenService refreshTokenService ;

	@Override
	@Transactional
	public Session findOrCreateSession(UUID userId, String deviceId) {
		
		sessionRepository
		        .findByUserIdAndDeviceInfoAndRevokedAtIsNull(userId, deviceId)
		        .ifPresent(existing -> {
		        	revokeSession(existing) ;
		        	accessTokenService.revoke(existing.getSid()) ;
		        	refreshTokenService.revoke(existing.getSid()) ;
		            // no explicit save needed (managed entity)
		        }) ;
		
		// 2️⃣ Enforce max active sessions (after revocation)
	    long activeSessions = sessionRepository.countActiveSessions(userId);
	    if (activeSessions >= MAX_ACTIVE_SESSIONS) {
	        throw new MaximumLimitReachedException(
	            "Maximum number of active logins reached (" + MAX_ACTIVE_SESSIONS + ")"
	        ) ;
	    }

	    // 3️⃣ Always create a NEW session
	    Session newSession = Session.builder()
	        .sid(UUID.randomUUID())
	        .userId(userId)
	        .deviceInfo(deviceId)
	        .build() ;

	    return sessionRepository.save(newSession) ;
		
	}
	
	@Override
	@Transactional
    public void revokeSession(UUID sid) {
        sessionRepository.findById(sid).ifPresent(session -> {
            session.setRevokedAt(Instant.now());
            sessionRepository.save(session);
        });
    }
	
	@Override
	@Transactional
    public void revokeSession(Session session) {
		session.setRevokedAt(Instant.now()) ;
        sessionRepository.save(session) ;   
    }
	
	@Override
	@Transactional
    public int revokeAllUserSession(UUID userId) {
		List<UUID> allActiveSessions = sessionRepository.findActiveSessionSidsByUserId(userId) ;
		
        int revokeByUserId = sessionRepository.revokeByUserId(userId, Instant.now()) ;
        refreshTokenService.revoke(allActiveSessions) ;
        accessTokenService.revoke(allActiveSessions) ;
        
        return revokeByUserId ;
    }
	
	@Override
    public Session findActiveSession(UUID sid) {
        return sessionRepository.findById(sid)
            .filter(s -> s.getRevokedAt() == null)
            .orElseThrow(() -> new InvalidTokenIdentifierException("Session expired")) ;
    }

}
