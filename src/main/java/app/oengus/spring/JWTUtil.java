package app.oengus.spring;

import app.oengus.entity.model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.Serial;
import java.io.Serializable;
import java.security.Key;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JWTUtil implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Value("${oengus.jwt.secret}")
    private String secret;

    @Value("${oengus.jwt.expiration}") // 604800
    private String expirationTime;

    public Claims getAllClaimsFromToken(final String token) {
        return Jwts.parserBuilder()
            .setSigningKey(getKey())
            .requireIssuer("OengusIO")
            .build()
            .parseClaimsJws(token)
            .getBody();
    }

    @Deprecated
    private boolean isTokenExpired(final String token) {
        return !this.isTokenValid(token);
    }

    public boolean isTokenValid(final String token) {
        try {
            final Claims claims = this.getAllClaimsFromToken(token);

            return claims.getExpiration().before(new Date());
        } catch (final ExpiredJwtException | SecurityException e) {
            return false;
        }
    }

    public String generateToken(final User user) {
        final Map<String, Object> claims = new HashMap<>();
        claims.put("role", user.getRoles());
        claims.put("enabled", user.isEnabled());
        claims.put("id", user.getId());
        return this.doGenerateToken(claims, user.getUsername(), user.getId());
    }

    private String doGenerateToken(final Map<String, Object> claims, final String username, final int id) {
        final long expirationTimeLong = Long.parseLong(this.expirationTime); //in second

        final Instant now = Instant.now();

        final Date createdDate = new Date();
        final Date expirationDate = new Date(createdDate.getTime() + (expirationTimeLong * 1000));
        return Jwts.builder()
            .setClaims(claims)
            .setSubject(username)
            .setIssuer("OengusIO")
            .setIssuedAt(createdDate)
            .setNotBefore(createdDate)
            .setId(Integer.toString(id))
            .setExpiration(expirationDate)
            .signWith(getKey())
            .compact();
    }

    @Deprecated
    public boolean validateToken(final String token) {
        return this.isTokenValid(token);
    }

    private Key getKey() {
        return Keys.hmacShaKeyFor(this.secret.getBytes());
    }

}
