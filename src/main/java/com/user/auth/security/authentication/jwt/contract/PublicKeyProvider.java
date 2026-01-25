package com.user.auth.security.authentication.jwt.contract;

import com.user.auth.dtos.JwkSet;

public interface PublicKeyProvider {
	JwkSet getPublicKey() ;
}
