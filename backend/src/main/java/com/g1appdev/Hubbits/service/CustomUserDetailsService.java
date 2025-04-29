package com.g1appdev.Hubbits.service;

import com.g1appdev.Hubbits.entity.UserEntity;
import com.g1appdev.Hubbits.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    private static final Logger logger = LoggerFactory.getLogger(CustomUserDetailsService.class);

    @Override
public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    logger.info("Attempting to load user by username: {}", username);

    // Fetch the UserEntity from the database
    UserEntity userEntity = userRepository.findByUsername(username)
            .orElseThrow(() -> {
                logger.warn("User not found with username: {}", username);
                return new UsernameNotFoundException("User not found");
            });

    // Log the username and the stored password for debugging purposes
    logger.info("User found: {} with stored password: {}", userEntity.getUsername(), userEntity.getPassword());

    // Return a Spring Security User object
    return new org.springframework.security.core.userdetails.User(
            userEntity.getUsername(),
            userEntity.getPassword(),
            new ArrayList<>()); // Add roles/authorities if needed
    }

}