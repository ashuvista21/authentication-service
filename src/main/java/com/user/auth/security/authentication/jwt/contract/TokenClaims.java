package com.user.auth.security.authentication.jwt.contract;

import java.util.Date;

public interface TokenClaims {
	String getSubject();
    Date getExpiration();
    String getIssuer();
    Object get(String key);
}
