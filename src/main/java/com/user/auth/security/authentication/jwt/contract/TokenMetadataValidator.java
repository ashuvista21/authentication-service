package com.user.auth.security.authentication.jwt.contract;

public interface TokenMetadataValidator {
	boolean validateTokenMetadata(String jti, String sid) ;
}
