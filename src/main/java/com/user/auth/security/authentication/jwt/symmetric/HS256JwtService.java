package com.user.auth.security.authentication.jwt.symmetric;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.SecretKey;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.user.auth.security.authentication.jwt.adapter.JjwtClaimsAdapter;
import com.user.auth.security.authentication.jwt.contract.TokenClaims;
import com.user.auth.security.authentication.jwt.contract.TokenGenerator;
import com.user.auth.security.authentication.jwt.contract.TokenParser;
import com.user.auth.security.authentication.jwt.contract.TokenValidator;
import com.user.auth.security.authentication.service.TokenIntrospectionService;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class HS256JwtService implements TokenGenerator, TokenParser, TokenValidator {

	//private static final String SECRET = "your-very-secure-secret-key-your-very-secure-secret-key"; 
    // must be at least 32 chars (256-bit) for HS256
	// ✅ Now using SecretKey directly (generated once & stored securely)
    private final SecretKey secretKey ;

    private static final long EXPIRATION_TIME = 1000 * 60 * 60 ; // 1 hour
    
    private TokenIntrospectionService introspectionService ;

	@Override
	public boolean isTokenValid(String token) {
        return !isTokenExpired(token) ;
	}

	@Override
	public boolean validateTokenSignature(String token) {
		try {
            Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token) ;
            return true ;
        } catch (JwtException | IllegalArgumentException e) {
            return false ;
        }
	}

	@Override
	public TokenClaims extractClaim(String token) {
		Claims claims = extractAllClaims(token) ;
		return new JjwtClaimsAdapter(claims) ;
	}

	@Override
	public String extractUsername(String token) {
		return extractClaim(token).getSubject() ;
	}

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
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact() ;
	}

	private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
	
	private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }
	
	private Date extractExpiration(String token) {
        return extractClaim(token).getExpiration() ;
    }

	@Override
	public boolean introspectToken(String token) {
		return isTokenValid(token) && introspectionService.introspect(token) ;
	}
}
