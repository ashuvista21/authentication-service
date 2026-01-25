package com.user.auth.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
	
	@Id
    @Column(updatable = false, nullable = false)
    private String uuid;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String name;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRoles role;  // e.g. "USER", "ADMIN"
    
    // 🔑 Security flags
    @Builder.Default
    @Column(nullable = false)
    private boolean enabled = true;
    
    @Builder.Default
    @Column(nullable = false)
    private boolean accountNonExpired = true;
    
    @Builder.Default
    @Column(nullable = false)
    private boolean accountNonLocked = true;
    
    @Builder.Default
    @Column(nullable = false)
    private boolean credentialsNonExpired = true;
}
