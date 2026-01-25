package com.user.auth.security.authentication.jwt;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthPrincipal {
	private String username ;
	private String sid ;
	private String jti ;
	private List<String> roles ;
}
