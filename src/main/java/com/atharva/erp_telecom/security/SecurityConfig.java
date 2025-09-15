package com.atharva.erp_telecom.security;


import com.atharva.erp_telecom.service.UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/*
    Purpose of a @Configuration annotation:
        - This class contains methods (@Bean) that define how to create and configure objects, and those objects should be managed by the Spring container.

    Advantages:
        - Spring sees @Configuration → it scans the class during startup.
        - For every method annotated with @Bean, it calls the method once and registers the returned object in the ApplicationContext.
        - Later, when something requests PasswordEncoder or MyService, Spring provides the already-created bean.

    Key differences between @Component and @Configuration:
        - @Component → marks a class as a candidate for component scanning (Spring instantiates it automatically).
        - @Configuration → is specifically for defining bean factories via methods.

    Use the @Bean annotation for those methods who serve a generic purpose e.g. Password Generation, Standard Services, etc.

 */

@Configuration
public class SecurityConfig {
    private final UserService userService;

    public SecurityConfig(UserService userService) {
        this.userService = userService;
    }

    /*
         Using this as a Bean which store this in the application context. The pre-rendered PasswordEncoder object will
         help to call the encode(CharSequence rawPassword) method which will convert the raw password to an encrypted password
     */

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable) // disable CSRF for APIs
                .authorizeHttpRequests(auth -> auth
                        // allow register/login without token
                        .requestMatchers("/users/register", "/users/login").permitAll()
                        // protect everything else
                        .anyRequest().authenticated()
                )
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) ;// stateless


        return http.build();
    }

    // Removed the explicitly added provider
}
