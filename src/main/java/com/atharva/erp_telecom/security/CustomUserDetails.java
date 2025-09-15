package com.atharva.erp_telecom.security;

import com.atharva.erp_telecom.entity.Users;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.*;


// NOTES for UserDetails:
/*
    # What is UserDetails?
        It’s part of Spring Security.
        Represents a principal (a user in the system) that Spring Security can work with.
        Defines what information Spring needs to know about a user:
        Username
        Password
        Authorities (roles/permissions)
        Account status flags (active, locked, expired, etc.)

        Basically, Spring Security doesn’t care about your User entity directly.
        Instead, it works with UserDetails.

        That’s why we wrap your entity into CustomUserDetails.

    # The Interface (simplified):

        public interface UserDetails extends Serializable {
            Collection<? extends GrantedAuthority> getAuthorities();
            String getPassword();
            String getUsername();
            boolean isAccountNonExpired();
            boolean isAccountNonLocked();
            boolean isCredentialsNonExpired();
            boolean isEnabled();
        }

    # Responsibilities of Each Method
        1. getAuthorities()
            Returns roles or permissions (wrapped as GrantedAuthority).
            Example: [ROLE_ADMIN, ROLE_USER].
            Spring uses this to decide access rights.
        2. getPassword()
            Encrypted/hashed password (never plain text).
            Used internally for login authentication.
        3. getUsername()
            The unique identifier for login (can be username, email, etc.).
        4. isAccountNonExpired()
            Returns true if the account hasn’t expired.
            You can hardcode true if you don’t use this check.
        5. isAccountNonLocked()
            Returns true if the user is not locked.
            Useful for blocking accounts after multiple failed logins.
        6. isCredentialsNonExpired()
            Returns true if password/credentials haven’t expired.
            Useful for password rotation policies.
        7. isEnabled()
            Returns true if the account is active.
            We mapped it to your IS_ACTIVE column.

    # Why Not Use Your User Entity Directly?
        Spring Security is decoupled from your persistence layer.
        Your User might have fields like firstName, lastName, customerId → irrelevant to security.
        By using UserDetails, Spring Security only cares about what it needs.

    # Correlation:
        Think of UserDetails as a bridge/adapter:
        Your User entity = database representation.
        UserDetails = Spring Security’s “expected format.”
        Your CustomUserDetails is the adapter between them.

👉 Next: Once you understand UserDetails, the natural pair is UserService (which you just implemented). Together, these two are how Spring loads and authenticates users.

Would you like me to also explain how UserService + UserDetails are called behind the scenes during login?

 */

public class CustomUserDetails implements UserDetails {

    private final Users user;

    public CustomUserDetails(Users user){
        this.user = user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Set<GrantedAuthority> authorities = new HashSet<>();
        if (user.getRoles() != null) {
            user.getRoles().forEach(role ->
                    authorities.add(new SimpleGrantedAuthority(role.getRoleName()))
            );
        }
        return authorities;
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUserName();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return UserDetails.super.isCredentialsNonExpired();
    }

    @Override
    public boolean isEnabled() {
        return user.getEnabled();
    }
}
