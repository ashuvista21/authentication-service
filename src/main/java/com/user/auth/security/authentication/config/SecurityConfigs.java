package com.user.auth.security.authentication.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.filter.OncePerRequestFilter;

import com.user.auth.security.authentication.filter.JwtAuthenticationFilter;
import com.user.auth.security.authentication.handler.JwtAccessDeniedHandler;
import com.user.auth.security.authentication.handler.JwtAuthEntryPoint;

@Configuration
public class SecurityConfigs {
	
	@Bean
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder(12) ;
	}
	
	@Bean
	AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
	    return config.getAuthenticationManager();
	}
	
	// @Qualifier("asymmtericJwtAuthenticationFilter") OncePerRequestFilter jwtAuthenticationFilter , JwtAuthEntryPoint jwtAuthEntryPoint
	// Use above if you want to pass OncePerRequestFilter in the function signature, above will select you qualifier bean among the two filter which you created conditionally and Spring Boot default filters
	// Or just implement the marker interface on both filter and use in function signature, with this there will be no confusion among filters
	// Because when you pass OncePerRequestFilter, you have multiple beans of this type so you have to use Qualifier Annotation
	// To avoid @Qualifier annotation we use marker interface on our filters
	@Bean
	SecurityFilterChain securityFilterChain(HttpSecurity http, JwtAuthenticationFilter jwtAuthenticationFilter , JwtAuthEntryPoint jwtAuthEntryPoint, JwtAccessDeniedHandler jwtAccessDeniedHandler) throws Exception {
	    http
	        .csrf(csrf -> csrf.disable()) // disable CSRF for APIs
	        .authorizeHttpRequests(auth -> auth
	            .requestMatchers("/signup/**", "/password/**", "/auth/**").permitAll() // allow unauthenticated access
	            .requestMatchers("/user/**").hasAnyRole("USER", "ADMIN")
	            .requestMatchers("/admin/**").hasRole("ADMIN")
	            .anyRequest().authenticated() // everything else requires JWT
	        )
	        .exceptionHandling(ex -> ex
	        							.authenticationEntryPoint(jwtAuthEntryPoint)
	        							.accessDeniedHandler(jwtAccessDeniedHandler))
	        .addFilterBefore((OncePerRequestFilter) jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
	    	.httpBasic(httpBasic -> httpBasic.disable()) ; // no basic auth, only JWT

	    // Later you will plug in your JWT filter here
	    return http.build();
	}
	
}
