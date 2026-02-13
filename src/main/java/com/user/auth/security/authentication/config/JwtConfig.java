package com.user.auth.security.authentication.config;

import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import com.user.auth.config.JwtProperties;

import java.io.InputStream;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

import javax.crypto.SecretKey;

@Configuration
@RequiredArgsConstructor
public class JwtConfig {
	
	private final JwtProperties jwtProperties ;
	private final ResourceLoader resourceLoader ;
	
	// ---------- SYMMETRIC ----------
    @Bean
    SecretKey secretKey() {
    	String secret = jwtProperties.secret() ;
    	
        if (secret == null || secret.isBlank()) {
            // ✅ Generate random key if not provided
            System.out.println("⚠️ No JWT secret configured. Generating random key (NOT persistent).");
            return Keys.secretKeyFor(SignatureAlgorithm.HS256);
        } else {
            // ✅ Use provided Base64 secret from config
            byte[] keyBytes = Decoders.BASE64.decode(secret);
            return Keys.hmacShaKeyFor(keyBytes);
        }
    }
    
    // ---------- ASYMMETRIC ----------

    @Bean
    RSAPublicKey rsaPublicKey() throws Exception {
    	Resource publicKeyResource = resourceLoader.getResource(jwtProperties.publicKeyPath()) ;
    	
        try (InputStream is = publicKeyResource.getInputStream()) {
            String key = new String(is.readAllBytes())
                    .replace("-----BEGIN PUBLIC KEY-----", "")
                    .replace("-----END PUBLIC KEY-----", "")
                    .replaceAll("\\s+", "");
            byte[] decoded = Base64.getDecoder().decode(key);
            X509EncodedKeySpec spec = new X509EncodedKeySpec(decoded);
            return (RSAPublicKey) KeyFactory.getInstance("RSA").generatePublic(spec);
        }
    }

    @Bean
    PrivateKey privateKey() throws Exception {
    	Resource privateKeyResource = resourceLoader.getResource(jwtProperties.privateKeyPath()) ;
    	
        try (InputStream is = privateKeyResource.getInputStream()) {
            String key = new String(is.readAllBytes())
                    .replace("-----BEGIN PRIVATE KEY-----", "")
                    .replace("-----END PRIVATE KEY-----", "")
                    .replaceAll("\\s+", "");
            byte[] decoded = Base64.getDecoder().decode(key);
            PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(decoded);
            return KeyFactory.getInstance("RSA").generatePrivate(spec);
        }
    }
   
}

