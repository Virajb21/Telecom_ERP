package com.atharva.erp_telecom.service;





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

👉 Next: Once you understand UserDetails, the natural pair is UserDetailsService (which you just implemented). Together, these two are how Spring loads and authenticates users.

Would you like me to also explain how UserDetailsService + UserDetails are called behind the scenes during login?

 */

public class UserDetailsMapping {
}
