package com.user.auth.security.authentication.service;

import java.time.Instant;
import java.util.Date;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.nimbusds.jwt.SignedJWT;
import com.user.auth.entities.AccessToken;
import com.user.auth.entities.Session;
import com.user.auth.entities.User;
import com.user.auth.services.AccessTokenService;
import com.user.auth.services.SessionService;
import com.user.auth.services.UserService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TokenIntrospectionService {
	
	private final UserService userService ;
	private final SessionService sessionService ;
	private final AccessTokenService accessTokenService ;
	
	public boolean introspect(String token) {

        try {
            SignedJWT signedJWT = SignedJWT.parse(token) ;

            // 1 Validate expiry
            Date expiration = signedJWT.getJWTClaimsSet().getExpirationTime() ;
            if (expiration.before(new Date())) {
            	return false ;
            }

            // 2 Extract claims
            String sid = signedJWT.getJWTClaimsSet().getStringClaim("sid") ;
            String jti = signedJWT.getJWTClaimsSet().getJWTID() ;
            String username = signedJWT.getJWTClaimsSet().getSubject() ;

            // 3 Fetch session and validate
            Session session = sessionService.findActiveSession(UUID.fromString(sid)) ;

            if (session == null) return false ;
            if (session.getRevokedAt() != null) return false ;
            
            // 4 Fetch access token and validate
            AccessToken accessToken = accessTokenService.getAccessTokenBySession(UUID.fromString(sid)) ;
            
            if (accessToken == null) return false ;
            if (accessToken.getRevokedAt() != null) return false ;
            if (accessToken.getExpiresAt().isBefore(Instant.now())) return false ;
            if (!accessToken.getJti().equals(UUID.fromString(jti))) return false ;
            
            // 5 Fetch user and validate
            User user = userService.getUser(session.getUserId().toString()) ;
            
            if(user == null) return false ;
            if (!user.getUsername().equals(username)) return false ;
            
            // 6 All good
            return true;

        } catch (Exception e) {
            return false ;
        }
    }
}
