package com.atharva.erp_telecom.security;


import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.*;
import java.util.function.Function;

//NOTES:

/*
    JWT Structure:
        1. Header (decoded)
            This section specifies that the token is a JWT and is signed using the HMAC SHA256 algorithm.
            json
            {
              "alg": "HS256",
              "typ": "JWT"
            }

        2. Payload (decoded)
            The payload contains the "claims," or the user-related data. For this example, the token identifies a user with the ID 1234567890,
            the name "John Doe," and the timestamp when the token was issued (iat).
            json
            {
              "sub": "1234567890",
              "name": "John Doe",
              "iat": 1516239022
            }

        3. Signature
            The signature is created by taking the Base64-URL encoded header, the Base64-URL encoded payload, and a secret key known only to the server.
            This is an example of the signature output.
            text
            SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c
            Use code with caution.

            Complete JWT
            When combined, the three parts, separated by dots, form the complete JWT that is passed between the client and server.
            e.g. eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0
            IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c
 */

/*
    About CLAIMS AND SUBJECTS IN JWT:
        1. Subject (setSubject(subject))
            The subject is usually the unique identifier of the user.
            In most apps, we put the username (or userId) as the subject.
            It answers the question: “Who is this token about?”

        2. Claims:
            Claims are key-value pairs inside the JWT payload.
            They carry extra information about the user or token.
            There are two types:
            - Standard claims (predefined by JWT spec):
                sub → subject (username)
                exp → expiration time
                iat → issued at
                iss → issuer
            - Custom claims (we define):
                e.g., role: "ADMIN", customerId: 42, region: "US"
 */

@Component
public class JwtUtils {

    private final String JWT_SECRET_KEY = System.getenv("JWT_SECRET_KEY");
    private final long JWT_TOKEN_VALIDITY =  60 * 60 * 1000;

    // Method to generate signing-key (Object of type Key) using the secret key which Java JWT(JJWT) requires.
    private Key getSignKey() {
        return Keys.hmacShaKeyFor(JWT_SECRET_KEY.getBytes(StandardCharsets.UTF_8));
    }

    public String generateToken(UserDetails userDetails){
        Map<String,Object> claims = new HashMap<>();
        claims.put("roles",userDetails.getAuthorities());
        return createToken(claims,userDetails.getUsername());
    }

    // About JJWT and JWTS
    /*
        1. Jwts Instance
            Jwts is a helper (factory) class from the JJWT library (io.jsonwebtoken package).
            It is not a bean, just a static utility class.
            It gives you entry points into the library:
            builder() → for creating JWTs.
            parserBuilder() → for validating & extracting claims from JWTs.
            Think of it like a toolbox with two main tools:
            🔨 builder() = make a new token.
            🔎 parserBuilder() = read/validate an existing token.

        2. parserBuilder()
            Jwts.parserBuilder()
                .setSigningKey(getSignKey())
                .build()
                .parseClaimsJws(token)
                .getBody();

            This is the newer, recommended way of parsing JWTs.
            Step by Step
            - parserBuilder()
            Returns a JwtParserBuilder object.
            Lets you configure parsing (e.g., set signing key, allowed clock skew).

            .setSigningKey(getSignKey())
            Configures the parser with your signing key.
            This key will be used to verify the token signature (integrity check).

            .build()
            Turns the builder into a ready-to-use JwtParser.

            .parseClaimsJws(token)
            Actually parses and validates the token.
            Throws an exception if the signature is invalid or the token is malformed.

            .getBody()
            Returns the claims (Claims object), which contains the payload (subject, roles, expiry, etc).

            3. Contrast with builder()
                Jwts.builder()
                    .setSubject("atharva")
                    .setIssuedAt(new Date())
                    .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60))
                    .signWith(getSignKey(), SignatureAlgorithm.HS256)
                    .compact();


                This is the writing mode: creating and signing JWTs.

                compact() finalizes and returns the token string.

                ⚖️ In Short

                Jwts = static entry point into the library.

                builder() = create/write tokens.

                parserBuilder() = configure + parse/validate tokens.
     */
    private String createToken(Map<String,Object> claims, String subject){
        Date now = new Date();
        Date expiry = new Date(now.getTime() + JWT_TOKEN_VALIDITY);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(now)
                .setExpiration(expiry)
                .signWith(SignatureAlgorithm.HS512, JWT_SECRET_KEY)
                .compact();
    }

    // Method to extract all the claims from the token.
    private Claims extractAllClaims(String token){
        return Jwts.parserBuilder()
                .setSigningKey(getSignKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // Method to extract one claim from all the claims extracted above.
    public <T> T extractGenericClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    // Method to extract username i.e. subject in this context.
    public String extractUsername(String token) {
        return extractGenericClaim(token, Claims::getSubject);
    }

    // Method to validate the existing token
    public boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    // Method to extract the expiration date from the token.
    public Date extractExpiration(String token) {
        return extractGenericClaim(token, Claims::getExpiration);
    }

    // Method to check the token is expired.
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }
}
