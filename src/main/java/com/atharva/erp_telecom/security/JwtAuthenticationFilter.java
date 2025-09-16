package com.atharva.erp_telecom.security;

import com.atharva.erp_telecom.exception.custom_exceptions.InvalidJwtAuthenticationException;
import com.atharva.erp_telecom.exception.custom_exceptions.MalformedJwtTokenException;
import com.atharva.erp_telecom.service.UserService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
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
    // IMPORTANT:
    /*
        NOTE:
        - This method runs before the controllers, hence any http related Custom exceptions implemented using @RestControllerAdvice
        will not be triggered, hence the thrown exceptions will be logged in the console.
        - To throw any custom exceptions like in package custom_exceptions using the @RestControllerAdvice, we have to use a method outside
        which mocks a mini-controller.

        When an exception happens inside a filter (like your JwtAuthenticationFilter), the request hasn’t yet reached Spring MVC’s controller pipeline. That means:
            Your @ControllerAdvice exception handlers are not active yet.
            Those only kick in once the request is being handled by a controller.
            Filters run before controllers. They’re part of the Servlet Filter Chain managed by Spring Security.
            So, if we just throw new InvalidJwtAuthenticationException(...) inside the filter, it bubbles up in the servlet container and ends up being logged to the console instead of being translated into a nice HTTP response body.

            👉 That’s why we had to manually populate the response (response.setStatus(...) and write JSON to the body). We basically acted as
            a “mini-controller” inside the filter to send a proper HTTP response to the client.

            Alternatives to avoid manual response writing:

            Custom AuthenticationEntryPoint
            You can register an AuthenticationEntryPoint bean in Spring Security. It acts as the global handler for all authentication
            failures (invalid/missing token, bad credentials, etc.).
            Spring Security will then call this handler whenever SecurityContext is empty or the authentication fails.

            Exception translation filter
            Spring Security has an ExceptionTranslationFilter that intercepts exceptions from downstream filters.
            If you throw the right type (e.g., AuthenticationException), it will automatically delegate to your AuthenticationEntryPoint.

            ⚖️ In short:
            - Inside controller layer → @ControllerAdvice works.
            - Inside filter layer → must use AuthenticationEntryPoint or manually populate response.
     */

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        //System.out.println("Entered the filter\nURI:"+request.getRequestURI());
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

        try {
            if(authHeader != null && authHeader.startsWith("Bearer ")){
                jwt = authHeader.substring(7);
                userName = jwtUtils.extractUsername(jwt);
            }

            if(userName != null && SecurityContextHolder.getContext().getAuthentication() == null){
                UserDetails userDetails = userService.loadUserByUsername(userName);
                if(jwtUtils.validateToken(jwt,userDetails)){
                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(userDetails,null,userDetails.getAuthorities());
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }else{
                    throw new InvalidJwtAuthenticationException("Invalid or expired JWT token.");
                }
            }
            filterChain.doFilter(request,response);
        } catch (io.jsonwebtoken.MalformedJwtException ex) {
            writeErrorResponse(response, "Malformed JWT token");
        } catch (io.jsonwebtoken.ExpiredJwtException ex) {
            writeErrorResponse(response, "JWT token expired");
        } catch (io.jsonwebtoken.SignatureException ex) {
            writeErrorResponse(response, "Invalid JWT signature");
        } catch (Exception ex) {
            writeErrorResponse(response, "Invalid authentication request");
        }
    }

    // Defining outside the filter because the overridden method directly prints the .
    private void writeErrorResponse(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.getWriter().write("{\"error\":\"" + message + "\"}");
    }
}
