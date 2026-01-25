package com.user.auth.dtos;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class JwkSet {
    private List<Jwk> keys;
}
