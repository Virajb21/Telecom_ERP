package com.atharva.erp_telecom.service;


import com.atharva.erp_telecom.entity.Roles;
import com.atharva.erp_telecom.entity.Users;
import com.atharva.erp_telecom.repository.RolesRepository;
import com.atharva.erp_telecom.repository.UserRepository;
import com.atharva.erp_telecom.security.JwtUtils;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserAuthService {

    private final UserRepository userRepository;
    private final RolesRepository rolesRepository;
    private final PasswordEncoder passwordEncoder;              // injected from the SecurityConfig class
    private final AuthenticationManager authenticationManager;  // injected from the SecurityConfig class
    private final UserService userService;
    private final JwtUtils jwtUtils;

    public UserAuthService(UserRepository userRepository, RolesRepository rolesRepository, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager, UserService userService, JwtUtils jwtUtils) {
        this.userRepository = userRepository;
        this.rolesRepository = rolesRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.userService = userService;
        this.jwtUtils = jwtUtils;
    }

    // Method to register a new user.
    public Optional<Users> registerNewUser(Users user, String roleName){
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        if(roleName != null){
            Roles role = rolesRepository.findByName(roleName);
            user.getRoles().add(role);
        }
        return Optional.of(userRepository.save(user));
    }

    // Authenticate existing user and return a JWT token
    public String authenticate(String username,String password){
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password)
        );

        UserDetails userDetails = userService.loadUserByUsername(username);
        return jwtUtils.generateToken(userDetails);
    }
}
