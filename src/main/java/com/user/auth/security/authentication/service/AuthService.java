package com.user.auth.security.authentication.service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.util.Pair;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.user.auth.dtos.JwkSet;
import com.user.auth.dtos.LoginRequest;
import com.user.auth.dtos.LogoutScope;
import com.user.auth.entities.AccessToken;
import com.user.auth.entities.GrantTypes;
import com.user.auth.entities.RefreshToken;
import com.user.auth.entities.Session;
import com.user.auth.exceptions.auth.DeviceMismatchException;
import com.user.auth.exceptions.auth.InvalidRequestPayloadException;
import com.user.auth.security.authentication.jwt.JwtMetadata;
import com.user.auth.security.authentication.jwt.JwtServiceFactory;
import com.user.auth.security.authentication.jwt.contract.JwtAlgorithm;
import com.user.auth.security.authentication.jwt.contract.PublicKeyProvider;
import com.user.auth.security.authentication.jwt.contract.TokenGenerator;
import com.user.auth.security.authentication.jwt.contract.TokenMetadataValidator;
import com.user.auth.security.authentication.jwt.contract.TokenParser;
import com.user.auth.security.authentication.jwt.contract.TokenValidator;
import com.user.auth.security.authentication.jwt.utils.JwtUtils;
import com.user.auth.security.userdetails.CustomUserDetails;
import com.user.auth.security.userdetails.CustomUserDetailsService;
import com.user.auth.services.AccessTokenService;
import com.user.auth.services.RefreshTokenService;
import com.user.auth.services.SessionService;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {
	
	@Value("${auth.max-active-sessions}")
    private int MAX_ACTIVE_SESSIONS ;
	
	private final JwtServiceFactory jwtServiceFactory ;
	
    private final AuthenticationManager authenticationManager ;
    private final CustomUserDetailsService userDetailsService ;
    
    private final AccessTokenService accessTokenService ;
    private final RefreshTokenService refreshTokenService ;
    private final SessionService sessionService ;

 // ✅ Spring-way using AuthenticationManager
    public Pair<String, String> login(LoginRequest request, String alg) { 	
    	if(request.getGrantType().equals(GrantTypes.PASSWORD)) {
    		return loginWithPassword(request, alg) ;
    	}
    	
    	if(request.getGrantType().equals(GrantTypes.REFRESH_TOKEN)) {
    		return loginWithRefreshToken(request, alg) ;
    	}
    	
    	throw new InvalidRequestPayloadException("Unsupported grant type: " + request.getGrantType()) ;    
    }
    
    private Pair<String, String> loginWithPassword(LoginRequest request, String alg) {
    	Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        ) ;
    	
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal() ;
        
        if (!userDetails.isEnabled()) {
            throw new DisabledException("User account is disabled") ;
        }
        if (!userDetails.isAccountNonLocked()) {
            throw new LockedException("User account is locked") ;
        }
        if (!userDetails.isAccountNonExpired()) {
            throw new AccountExpiredException("User account expired") ;
        }
        if (!userDetails.isCredentialsNonExpired()) {
            throw new CredentialsExpiredException("User credentials expired") ;
        }
        
        Session session = sessionService.findOrCreateSession(UUID.fromString(userDetails.getUuid()), request.getDeviceId()) ;

        RefreshToken refreshToken = refreshTokenService.rotateWithSession(session) ;
        
        AccessToken accessToken = accessTokenService.createAccessToken(session) ;
        
        TokenGenerator tokenGenerator = jwtServiceFactory.getTokenGenerator(JwtAlgorithm.valueOf(alg)) ;
        
        Map<String, Object> headers = new HashMap<>() ;
        headers.put("typ", "JWT") ;
        
        Map<String, Object> claims = new HashMap<>() ;
        claims.put("jti", accessToken.getJti().toString()) ;
        claims.put("sid", accessToken.getSid().toString()) ;
        
        return Pair.of(tokenGenerator.generateToken(headers, claims, userDetails), refreshToken.getRefreshToken().toString()) ;
    }
    
    private Pair<String, String> loginWithRefreshToken(LoginRequest request, String alg) {
    	
    	RefreshToken oldRefreshToken = refreshTokenService.validate(UUID.fromString(request.getRefreshToken())) ;

        Session session = sessionService.findActiveSession(oldRefreshToken.getSid()) ;
        
        if(!session.getDeviceInfo().equals(request.getDeviceId()))
        	throw new DeviceMismatchException("Invalid Refresh Token") ;

        RefreshToken newRefreshToken = refreshTokenService.rotateWithRefreshToken(oldRefreshToken) ;
        
        AccessToken accessToken = accessTokenService.createAccessToken(session) ;
        
        CustomUserDetails userDetails = userDetailsService.loadByUuid(session.getUserId().toString());

        if (!userDetails.isEnabled()) {
            throw new DisabledException("User account is disabled");
        }
        if (!userDetails.isAccountNonLocked()) {
            throw new LockedException("User account is locked");
        }
        if (!userDetails.isAccountNonExpired()) {
            throw new AccountExpiredException("User account expired");
        }
        if (!userDetails.isCredentialsNonExpired()) {
            throw new CredentialsExpiredException("User credentials expired");
        }
        
        TokenGenerator tokenGenerator = jwtServiceFactory.getTokenGenerator(JwtAlgorithm.valueOf(alg)) ;
        
        Map<String, Object> headers = new HashMap<>() ;
        headers.put("typ", "JWT") ;
        
        Map<String, Object> claims = new HashMap<>() ;
        claims.put("jti", accessToken.getJti().toString()) ;
        claims.put("sid", accessToken.getSid().toString()) ;

        return Pair.of(
                tokenGenerator.generateToken(headers, claims, userDetails),
                newRefreshToken.getRefreshToken().toString()
        ) ;
    }
    
    public int logout(LogoutScope scope, String sid) {
    	UUID actualSid = UUID.fromString(sid) ;
    	
    	if(scope.equals(LogoutScope.CURRENT)) {
    		sessionService.revokeSession(actualSid) ;
    		refreshTokenService.revoke(actualSid) ;
    		accessTokenService.revoke(actualSid) ;
    	}
    	else if(scope.equals(LogoutScope.ALL)) {
    		Session activeSession = sessionService.findActiveSession(actualSid) ;
    		UUID userId = activeSession.getUserId() ;
    		return sessionService.revokeAllUserSession(userId) ;
    	}
    	else {
    		throw new InvalidRequestPayloadException("Inavlid scope parameter") ;
    	}
    	
    	return 1 ;
    }

    public boolean verifyToken(String token) {
    	JwtAlgorithm alg = JwtUtils.extractAlgorithmFamily(token) ;
    	
    	if(alg.family().equals(JwtAlgorithm.Family.ASYMMETRIC)) {
    		TokenMetadataValidator tokenMetadataValidator = jwtServiceFactory.getTokenMetadataValidator(alg) ;
    		JwtMetadata jwtMetadata = JwtUtils.extractMetadataFromJWTToken(token) ;
    		return tokenMetadataValidator.validateTokenMetadata(jwtMetadata.jti(), jwtMetadata.sid()) ;
    	}
    	
    	if(alg.family().equals(JwtAlgorithm.Family.SYMMETRIC)) {
    		TokenValidator tokenValidator = jwtServiceFactory.getTokenValidator(alg) ;
        	return tokenValidator.validateTokenSignature(token) ;
    	}
        
    	return false ;
    }
    
    public Claims getClaims(String token) {
    	JwtAlgorithm alg = JwtUtils.extractAlgorithmFamily(token) ;
        TokenParser tokenParser = jwtServiceFactory.getTokenParser(alg); 
    	return tokenParser.extractClaim(token, Function.identity()) ;
    }
    
    public JwkSet getRSAPublicKey(String alg) {
    	PublicKeyProvider publicKeyProvider = jwtServiceFactory.getPublicKeyProvider(JwtAlgorithm.valueOf(alg)) ;
    	return publicKeyProvider.getPublicKey() ;
    }
}
