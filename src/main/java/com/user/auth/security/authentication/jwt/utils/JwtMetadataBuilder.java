package com.user.auth.security.authentication.jwt.utils;

import java.util.HashMap;
import java.util.Map;

public class JwtMetadataBuilder {
    private final Map<String, Object> claims = new HashMap<>();

    public JwtMetadataBuilder() {
    }

    public JwtMetadataBuilder add(String key, Object value) {
        if (key != null && value != null) {
            claims.put(key, value);
        }
        return this;
    }

    public Map<String, Object> build() {
        return new HashMap<>(claims);
    }
}
