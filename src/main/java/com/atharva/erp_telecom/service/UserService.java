package com.atharva.erp_telecom.service;


import com.atharva.erp_telecom.entity.Users;
import com.atharva.erp_telecom.repository.UserRepository;
import com.atharva.erp_telecom.security.CustomUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

// This service is only used to fetch the usernames.

@Service
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    @Autowired
    public UserService(UserRepository userRepository){
        this.userRepository = userRepository;
    }
    // This method is only used to fetch the UserDeta
    @Override
    public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
        Users user = userRepository.findByUserName(userName)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + userName));
        return new CustomUserDetails(user);
    }

    public String createUser(Users user){
        return userRepository.save(user).getUserName();
    }

    public Optional<Users> getUserByUserName(String userName){
        return userRepository.findByUserName(userName);
    }

}
