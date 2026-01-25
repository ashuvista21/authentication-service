package com.user.auth.security.authentication.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import validator.contract.TokenParser;
import validator.contract.TokenValidator;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.user.auth.security.authentication.jwt.AuthJwtServiceFactory;
import com.user.auth.security.authentication.jwt.AuthPrincipal;
import com.user.auth.security.authentication.jwt.contract.JwtAlgorithm;
import com.user.auth.security.authentication.jwt.utils.JwtUtils;

import java.io.IOException;
import java.text.ParseException;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Component
@ConditionalOnProperty(name = "jwt.algorithm", havingValue = "RS256")
@RequiredArgsConstructor
public class AsymmtericJwtAuthenticationFilter extends OncePerRequestFilter implements JwtAuthenticationFilter {

	private final AuthJwtServiceFactory authJwtServiceFactory;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

    	final String authHeader = request.getHeader("Authorization") ;
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response) ;
            return;
        }

        final String jwt = authHeader.substring(7) ;
        JwtAlgorithm alg = JwtUtils.extractAlgorithmFamily(jwt) ;

        if (!alg.family().equals(JwtAlgorithm.Family.ASYMMETRIC)) {
            filterChain.doFilter(request, response);
            return; // Not for this filter
        }

        TokenValidator tokenValidator = authJwtServiceFactory.getTokenValidator(alg);
        TokenParser tokenParser = authJwtServiceFactory.getTokenParser(alg);

        if (!tokenValidator.isTokenValid(jwt)) { // here null because no DB user check
            filterChain.doFilter(request, response);
            return;
        }

        // Extract claims
        String username = tokenParser.extractUsername(jwt);
        List<String> roles = tokenParser.extractClaim(jwt, claimsSet -> {
        	Object rolesClaims = claimsSet.getClaim("roles") ;
        	
        	if(rolesClaims instanceof List<?>) {
        		return ((List<?>) rolesClaims).stream()
        				.filter(String.class::isInstance)
        				.map(String.class::cast)
        				.toList() ;
         	}
        	else if(rolesClaims instanceof String) {
        		return List.of(((String) rolesClaims).split(",")) ;
        	}
        	
        	return List.of() ;
        }) ;
        String sid = tokenParser.extractClaim(jwt,
                claimsSet -> {
					try {
						return claimsSet.getStringClaim("sid");
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace() ;
						return "" ;
					}
				}
        ) ;

        String jti = tokenParser.extractClaim(jwt,
                claimsSet -> claimsSet.getJWTID()
        ) ;
        
        AuthPrincipal principal = AuthPrincipal.builder()
        		.jti(jti)
        		.sid(sid)
        		.username(username)
        		.roles(roles)
        		.build() ;
        
        Collection<GrantedAuthority> authorities = roles.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList()) ;

        // Build Authentication directly from JWT claims
        
        
        
        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(
                		principal,
                        null,
                        authorities
                );

        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authToken);

        filterChain.doFilter(request, response);
    }
}
