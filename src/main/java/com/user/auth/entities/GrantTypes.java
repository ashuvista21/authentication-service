package com.user.auth.entities;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum GrantTypes {
	
	PASSWORD,
	REFRESH_TOKEN;
	
	@JsonCreator
    public static GrantTypes fromString(String value) {
        return GrantTypes.valueOf(value.toUpperCase()) ;
    }

}
