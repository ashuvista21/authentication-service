package com.user.auth.security.authentication.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.user.auth.security.authentication.jwt.JwtServiceFactory;
import com.user.auth.security.authentication.jwt.contract.JwtAlgorithm;
import com.user.auth.security.authentication.jwt.contract.TokenParser;
import com.user.auth.security.authentication.jwt.contract.TokenValidator;
import com.user.auth.security.authentication.jwt.utils.JwtUtils;
import com.user.auth.security.userdetails.CustomUserDetailsService;

import java.io.IOException;

@Component
@ConditionalOnProperty(name = "jwt.algorithm", havingValue = "HS256")
@RequiredArgsConstructor
public class SymmtericJwtAuthenticationFilter extends OncePerRequestFilter implements JwtAuthenticationFilter {

	private final JwtServiceFactory jwtServiceFactory;
    private final CustomUserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String username;
        
        // 1. Check header
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }
        
        // 2. Extract token and algorithm
        jwt = authHeader.substring(7);
        JwtAlgorithm alg = JwtUtils.extractAlgorithmFamily(jwt) ;
        
        TokenValidator tokenValidator = jwtServiceFactory.getTokenValidator(alg) ;
        TokenParser tokenParser = jwtServiceFactory.getTokenParser(alg) ;
        
        username = tokenParser.extractUsername(jwt) ;     

        // 3. Validate token and set authentication
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            if (tokenValidator.isTokenValid(jwt, userDetails)) {
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities()
                        );

                authToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );

                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        // 4. Continue filter chain
        filterChain.doFilter(request, response);
    }
}
