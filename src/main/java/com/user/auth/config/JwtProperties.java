package com.user.auth.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "jwt")
public record JwtProperties (
		String algorithm,
		String secret,
		String privateKeyPath,
		String publicKeyPath) {
}
