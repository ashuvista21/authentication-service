package com.user.auth.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record PasswordResetRequest (
		
		@NotBlank(message = "recpient must not be null")
		@Email(message = "recpient must be a valid email")
        String recipient
){} ;
