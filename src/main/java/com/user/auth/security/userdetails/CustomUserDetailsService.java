package com.user.auth.security.userdetails;

import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.user.auth.entities.User;
import com.user.auth.repositories.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public CustomUserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Username not found: " + username)) ;
        return new CustomUserDetails(user);
    }
    
    public CustomUserDetails loadByUuid(String uuid) {

        User user = userRepository.findById(uuid)
            .orElseThrow(() ->
                new UsernameNotFoundException("Username not found")
            ) ;

        return CustomUserDetails.from(user) ;
    }
}
