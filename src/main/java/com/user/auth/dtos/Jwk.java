package com.user.auth.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Jwk {
    private String kty;  // Key Type (RSA, EC, etc.)
    private String alg;  // Algorithm (RS256, etc.)
    private String use;  // "sig" for signature
    private String kid;  // Key ID (optional but recommended)
    private String n;    // Modulus (for RSA)
    private String e;    // Exponent (for RSA)
}
