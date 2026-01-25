package com.user.auth.services;

import java.util.List;
import java.util.UUID;

import com.user.auth.entities.AccessToken;
import com.user.auth.entities.Session;

public interface AccessTokenService {
	AccessToken createAccessToken(Session session) ;
	AccessToken createAccessToken() ;
	void revoke(UUID sid) ;
	void revoke(List<UUID> sids) ;
	AccessToken getAccessTokenBySession(UUID sid) ;
}
