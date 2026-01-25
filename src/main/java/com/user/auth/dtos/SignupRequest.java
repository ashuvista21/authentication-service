package com.user.auth.dtos;

import com.user.auth.entities.UserRoles;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SignupRequest {

    @NotBlank(message = "Username cannot be blank")
    @Email(message = "Invalid email format")
    private String username;

    @NotNull(message = "Password cannot be null")
    @Size(min = 8, max = 15, message = "Password must be between 8 and 15 characters")
    private String password;

    @NotBlank(message = "Name cannot be blank")
    private String name;
    
    @NotNull(message = "Role cannot be null")
    private UserRoles role;
    
    public void setUsername(String username) {
    	this.username = username.toLowerCase().trim() ;
    }

}
