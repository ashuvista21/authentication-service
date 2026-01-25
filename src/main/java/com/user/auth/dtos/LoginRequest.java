package com.user.auth.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.user.auth.entities.GrantTypes;
import com.user.auth.validation.PasswordFlow;
import com.user.auth.validation.RefreshTokenFlow;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginRequest {
	
	@NotNull(message = "Grant type is required")
	@JsonProperty("grant_type")
	private GrantTypes grantType ;
	
	@NotBlank(message = "Username is required", groups = PasswordFlow.class)
	@Email(message = "Invalid email format")
	private String username ;
	
	@NotBlank(message = "Password is required", groups = PasswordFlow.class)
	private String password ;
	
	@NotBlank(message = "Refresh token is required", groups = RefreshTokenFlow.class)
	@JsonProperty("refresh_token")
	private String refreshToken ;
	
	@NotBlank(message = "Device ID is required")
	@JsonProperty("device_id")
	private String deviceId ;
	
	public void setUsername(String username) {
    	this.username = username.toLowerCase().trim() ;
    }

}
