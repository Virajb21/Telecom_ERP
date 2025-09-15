package com.atharva.erp_telecom.service;


import com.atharva.erp_telecom.dto.RegisterResponse;
import com.atharva.erp_telecom.entity.Roles;
import com.atharva.erp_telecom.entity.Users;
import com.atharva.erp_telecom.repository.RolesRepository;
import com.atharva.erp_telecom.repository.UserRepository;
import com.atharva.erp_telecom.security.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.management.relation.RoleNotFoundException;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserAuthService {

    private final UserRepository userRepository;
    private final RolesRepository rolesRepository;
    @Autowired
    private final PasswordEncoder passwordEncoder;              // injected from the SecurityConfig class
    @Autowired
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
    public RegisterResponse registerNewUser(Users user, Set<String> roleNames) {
        // Check if user already exists
        if (userRepository.existsByUserName(user.getUserName())) {
            String errorMessage = "Username already exists: " + user.getUserName();
            return new RegisterResponse(errorMessage);
        }
        // Encode password using BCryptPassword Encoding
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // Set default value for 'enabled'
        if (user.getEnabled() == null) user.setEnabled(true);
        RegisterResponse response = new RegisterResponse();
        Set<Roles> rolesSetToBeChecked =
                roleNames.stream()
                        .map(role -> {
                            try {
                                return rolesRepository.findByRoleName(role).orElseThrow(() -> new RoleNotFoundException("Role not found:" + role));
                            } catch (RoleNotFoundException e) {
                                throw new RuntimeException(e);
                            }
                        })
                        .collect(Collectors.toSet());
        user.setRoles(rolesSetToBeChecked);
        Users savedUser = userRepository.save(user);
        return new RegisterResponse("User with username:" + savedUser.getUserName() + " created successfully.");
    }

    // Authenticate existing user and return a JWT token
    public String authenticate(String username, String password) {
        // NOTE:
        /*
            This authentication manager is responsible for checking if the credentials match or not. It implicitly calls
            the wrapper UserService Service and findByUserName() method
         */

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password)
        );
        System.out.println("Inside the auth block");
        UserDetails userDetails = userService.loadUserByUsername(username);
        return jwtUtils.generateToken(userDetails);
    }
}
