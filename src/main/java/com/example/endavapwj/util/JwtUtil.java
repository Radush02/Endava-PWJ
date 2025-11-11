package com.example.endavapwj.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.function.Function;

/**
 * Provides utility methods for creating and validating JSON Web Tokens (JWT).
 *
 * <p>Access tokens are issued with a shorter validity period and are used for
 * authorizing requests. Refresh tokens are issued with a longer validity period
 * and are used for obtaining new access tokens without requiring the user to
 * log in again.
 *
 * <p>Both token types include the username as the subject claim and are signed
 * using an HMAC SHA-256 key derived from the configured secret.
 */
@Service
public class JwtUtil {
    @Value("${jwt.secret.key}")
    private String secretKey;


    /**
     * Generates a new access token for the given username.
     * The token is valid for 12 hours from the time of issuance.
     *
     * @param username the user identity to embed in the token
     * @return a signed JWT access token
     */
    public String generateToken(String username) {
        Instant now = Instant.now();
        return Jwts.builder()
                .claim("sub", username)
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plus(12, ChronoUnit.HOURS)))
                .signWith(
                        Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8)),
                        SignatureAlgorithm.HS256)
                .compact();
    }
    /**
     * Generates a new refresh token for the given username.
     * The token is valid for 30 days.
     *
     * @param username the user identity to embed in the refresh token
     * @return a signed JWT refresh token
     */
    public String generateRefreshToken(String username) {
        Instant now = Instant.now();
        return Jwts.builder()
                .claim("sub", username)
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plus(30, ChronoUnit.DAYS)))
                .signWith(Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8)), SignatureAlgorithm.HS256)
                .compact();
    }
    /**
     * Extracts the username (subject claim) from the provided token.
     *
     * @param token the JWT token to parse
     * @return the username contained in the token
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Extracts the expiration timestamp from the provided token.
     *
     * @param token the JWT token to parse
     * @return the token expiration date
     */
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Extracts a specific claim from the token using the provided claims resolver.
     *
     * @param token the JWT token to parse
     * @param claimsResolver a function that retrieves a specific claim from the token's claims
     * @param <T> the type of the value returned by the resolver
     * @return the resolved claim value
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        JwtParser jwtParser =
                Jwts.parser()
                        .setSigningKey(Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8)))
                        .build();
        Claims claims = jwtParser.parseClaimsJws(token).getBody();
        return claimsResolver.apply(claims);
    }

    /**
     * Validates that the token is correctly signed, not expired, and contains the expected username.
     *
     * @param token the JWT token to validate
     * @param username the username expected to be in the token
     * @return true if the token is valid, false otherwise
     */
    public boolean isTokenValid(String token, String username) {
        return (username.equals(extractUsername(token)) && !isTokenExpired(token));
    }

    /**
     * Checks whether the token has expired.
     *
     * @param token the JWT token to check
     * @return true if the token is expired, false otherwise
     */
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * Extracts the username from the JWT token.
     *
     * @return the username or null if the user isn't authenticated.
     */
    public String extractUsername() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }
}