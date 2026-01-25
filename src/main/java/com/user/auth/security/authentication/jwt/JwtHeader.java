package com.user.auth.security.authentication.jwt;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JwtHeader {
    private String alg ;
    private String typ ;
}
