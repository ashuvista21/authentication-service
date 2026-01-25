package com.user.auth.security.authentication.jwt.contract;

public enum JwtAlgorithm {

    // Asymmetric
    RS256(Family.ASYMMETRIC),
    ES256(Family.ASYMMETRIC),   // ECDSA
    PS256(Family.ASYMMETRIC),   // RSA-PSS

    // Symmetric
    HS256(Family.SYMMETRIC),
    HS384(Family.SYMMETRIC),
    HS512(Family.SYMMETRIC);

    public enum Family {
        ASYMMETRIC,
        SYMMETRIC
    }

    private final Family family;

    JwtAlgorithm(Family family) {
        this.family = family;
    }

    public Family family() {
        return family;
    }

    public static JwtAlgorithm fromHeader(String alg) {
        try {
            return JwtAlgorithm.valueOf(alg);
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("Unsupported JWT alg: " + alg);
        }
    }
}
