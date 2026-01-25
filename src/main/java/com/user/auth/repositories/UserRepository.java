package com.user.auth.repositories;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import com.user.auth.entities.User;

public interface UserRepository extends CrudRepository<User, String> {
	
	Optional<User> findByUsername(String username) ;
	boolean existsByUsername(String username) ;

}
