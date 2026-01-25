package com.user.auth.services;

import java.util.UUID;

import com.user.auth.entities.Session;

public interface SessionService {
	Session findOrCreateSession(UUID userId, String deviceId) ;
	void revokeSession(UUID sid) ;
	void revokeSession(Session session) ;
	Session findActiveSession(UUID sid) ;
	int revokeAllUserSession(UUID userId) ;
}
