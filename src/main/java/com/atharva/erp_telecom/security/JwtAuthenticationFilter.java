package com.atharva.erp_telecom.security;

import com.atharva.erp_telecom.service.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;


// NOTE
/*
    The purpose of this method is to scan every JWT token for every protected endpoint:
        The JWT Authentication Filter (usually extends OncePerRequestFilter) runs before every protected request. Its job is:
            Check if the request has a JWT token.
            Validate the token (signature, expiration).
            Load the user from DB if the token is valid.
            Set the user in Spring Security context so that downstream controllers and services know the request is authenticated.
            Essentially, it converts a JWT in the HTTP header into a Spring Security authentication object.

     Step-by-step breakdown
        1. final String authHeader = request.getHeader("Authorization");
            Reads the Authorization header from the request.
            Example header:
            Authorization: Bearer <JWT_TOKEN>

            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                jwt = authHeader.substring(7);
                username = jwtUtils.extractUsername(jwt);
            }


        2. Checks that the header exists and starts with "Bearer ".

            Extracts the token itself (jwt) and retrieves the username from the token claims.

            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = userService.loadUserByUsername(username);


        3. Only proceed if:

            The token has a username.

            No authentication exists yet in the SecurityContext.

            Loads UserDetails from the DB.

            if (jwtUtils.validateToken(jwt, userDetails)) {
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }


        4. Validates the token: correct signature, not expired.

            Creates a Spring Security Authentication object using UserDetails.
            Stores it in the SecurityContext, so Spring Security knows this request is authenticated.
            chain.doFilter(request, response);
            Passes the request to the next filter or controller.
            Downstream code (controllers) can now access the authenticated user via:
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        -- Why it’s needed

            Without this filter:

            Spring Security won’t know about the JWT token in the header.

            All protected endpoints (.anyRequest().authenticated()) will fail, even if the client has a valid JWT.

            The filter essentially bridges JWT authentication with Spring Security.

        Analogy:

            Think of it like a passport check at the airport:
            The JWT is your passport.
            The filter checks if the passport is valid.
            If valid, it grants you access to the country (sets authentication in SecurityContext).
            If invalid, you are blocked (401 Unauthorized).
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;
    private final UserService userService;

    public JwtAuthenticationFilter(JwtUtils jwtUtils, UserService userService) {
        this.jwtUtils = jwtUtils;
        this.userService = userService;
    }


    // Default overridden method
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        System.out.println("Entered the filter\nURI:"+request.getRequestURI());
        String servletPath = request.getServletPath();
        if(servletPath.equals("/users/register") || servletPath.equals("/users/login")){
            filterChain.doFilter(request,response);
            return ;
        }

        // Fetch the request header
        String HEADER_AUTHORIZATION = "Authorization";
        final String authHeader = request.getHeader(HEADER_AUTHORIZATION);
//        System.out.println("🔍 Headers in request:");
//        request.getHeaderNames().asIterator()
//                .forEachRemaining(h -> System.out.println(h + " = " + request.getHeader(h)));
//
//        System.out.println("Extracted Authorization header = " + authHeader);
        String userName = null;
        String jwt = null;

        if(authHeader != null && authHeader.startsWith("Bearer ")){
            jwt = authHeader.substring(7);
            try{
                userName = jwtUtils.extractUsername(jwt);
            }catch (Exception e){
                System.out.println("No bearer token in request...");
            }

        }

        if(userName != null && SecurityContextHolder.getContext().getAuthentication() == null){
            UserDetails userDetails = userService.loadUserByUsername(userName);
            if(jwtUtils.validateToken(jwt,userDetails)){
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(userDetails,null,userDetails.getAuthorities());
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }else{
                System.out.println("❌ JWT validation failed for: " + userName);
            }
        }
        filterChain.doFilter(request,response);
    }
}
