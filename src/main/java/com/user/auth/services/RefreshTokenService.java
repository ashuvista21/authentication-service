package com.user.auth.services;

import java.util.List;
import java.util.UUID;

import com.user.auth.entities.RefreshToken;
import com.user.auth.entities.Session;

public interface RefreshTokenService {
	RefreshToken rotateWithRefreshToken(RefreshToken refreshToken) ;
	RefreshToken rotateWithSession(Session session) ;
	RefreshToken validate(UUID refreshToken) ;
	void revoke(UUID sid) ;
	void revoke(List<UUID> sids) ;
}
