package com.atharva.erp_telecom.controller;


import com.atharva.erp_telecom.dto.AuthRequest;
import com.atharva.erp_telecom.dto.AuthResponse;
import com.atharva.erp_telecom.entity.Users;
import com.atharva.erp_telecom.security.JwtUtils;
import com.atharva.erp_telecom.service.UserAuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/users")
public class UserAuthController {

    private final UserAuthService userAuthService;

    public UserAuthController(UserAuthService userAuthService) {
        this.userAuthService = userAuthService;
    }

    // Endpoint to create a new user
    @PostMapping("/register")
    public ResponseEntity<Users> register(@RequestBody Users user, @RequestParam(name = "role", required = false) String role) {
        Users savedUser = userAuthService.registerNewUser(user, role);

//        return savedUser.map(ResponseEntity::ok)
//                .orElseGet(() -> new ResponseEntity<>(HttpStatus.BAD_REQUEST));
        return new ResponseEntity<>(savedUser,HttpStatus.CREATED);
    }

    // Endpoint to authenticate an exiting user and return a JWT token
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest authRequest){
        String jwtToken = userAuthService.authenticate(authRequest.getUsername(),authRequest.getPassword());
        return new ResponseEntity<>(new AuthResponse(jwtToken),HttpStatus.OK);
    }
}
