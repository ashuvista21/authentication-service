package com.user.auth.security.authentication.jwt.utils;

import java.io.IOException;
import java.math.BigInteger;
import java.security.interfaces.RSAPublicKey;
import java.util.Base64;
import java.util.Map;
import java.util.UUID;
import java.util.function.Supplier;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.user.auth.dtos.Jwk;
import com.user.auth.security.authentication.jwt.JwtHeader;
import com.user.auth.security.authentication.jwt.JwtMetadata;
import com.user.auth.security.authentication.jwt.contract.JwtAlgorithm;
import com.user.auth.security.authentication.jwt.contract.JwtAlgorithms;

public class JwtUtils {
	
	private static Supplier<String> getUuid = () -> UUID.randomUUID().toString() ;
    private static final ObjectMapper MAPPER = new ObjectMapper();
	
	public static JwtAlgorithms extractAlgorithm(String token) {
		String[] parts = token.split("\\.");
        String headerJson = new String(Base64.getUrlDecoder().decode(parts[0])) ;
        ObjectMapper mapper = new ObjectMapper() ;
        try {
            JwtHeader header = mapper.readValue(headerJson, JwtHeader.class) ;
            return JwtAlgorithms.valueOf(header.getAlg()) ; // 🔑 convert String to Enum
        } catch (IOException e) {
            throw new IllegalArgumentException("Invalid JWT header", e);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Unsupported JWT algorithm: " + e.getMessage(), e);
        }
	}
	
	public static JwtAlgorithm extractAlgorithmFamily(String token) {
		String[] parts = token.split("\\.");
        String headerJson = new String(Base64.getUrlDecoder().decode(parts[0])) ;
        ObjectMapper mapper = new ObjectMapper() ;
        try {
            JwtHeader header = mapper.readValue(headerJson, JwtHeader.class) ;
            return JwtAlgorithm.valueOf(header.getAlg()) ; // 🔑 convert String to Enum
        } catch (IOException e) {
            throw new IllegalArgumentException("Invalid JWT header", e);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Unsupported JWT algorithm: " + e.getMessage(), e);
        }
	}
	
	private static String base64UrlEncode(BigInteger value) {
        return Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(value.toByteArray());
    }

    public static Jwk fromRsaPublicKey(RSAPublicKey publicKey, String alg, String kid) {
        return new Jwk(
                "RSA",
                alg,
                "sig",
                kid,
                base64UrlEncode(publicKey.getModulus()),
                base64UrlEncode(publicKey.getPublicExponent())
        );
    }
    
    public static Map<String, Object> getJwtTokenMetadata(String[] params) {
    	JwtMetadataBuilder builder = new JwtMetadataBuilder() ;
    	
    	for(String each : params)
    		builder.add(each, getUuid.get()) ;
    	
    	return builder.build() ;
    }

    public static JwtMetadata extractMetadataFromJWTToken(String token) {
        try {
            String[] parts = token.split("\\.");
            if (parts.length != 3) {
                throw new IllegalArgumentException("Invalid JWT format");
            }

            String payloadJson = new String(
                Base64.getUrlDecoder().decode(parts[1])
            );

            JsonNode payload = MAPPER.readTree(payloadJson);

            String jti = payload.get("jti").asText();
            String sid = payload.get("sid").asText();

            return new JwtMetadata(jti, sid);

        } catch (Exception ex) {
            throw new IllegalArgumentException("Failed to extract JWT metadata", ex);
        }
    }
}
