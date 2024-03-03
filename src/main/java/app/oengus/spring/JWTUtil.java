package app.oengus.spring;

import app.oengus.entity.model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JWTUtil {
    private static final String JWT_ISS = "OengusIO";

    @Value("${oengus.jwt.secret}")
    private String secret;

    @Value("${oengus.jwt.expiration}") // 604800 seconds == 7 days
    private String expirationTime;

    public Claims getAllClaimsFromToken(final String token) {
        return Jwts.parser()
            .verifyWith(getKey())
            .requireIssuer(JWT_ISS)
            .build()
            .parseSignedClaims(token)
            .getPayload();
    }

    public boolean isTokenValid(final String token) {
        try {
            final Claims claims = this.getAllClaimsFromToken(token);

            return new Date().before(claims.getExpiration());
//            return true;
        } catch (final SecurityException | JwtException e) {
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
            .claims(claims)
            .subject(username)
            .issuer(JWT_ISS)
            .issuedAt(createdDate)
            .notBefore(createdDate)
            .expiration(expirationDate)
            .id(Integer.toString(id))
            .signWith(getKey())
            .compact();
    }

    private SecretKey getKey() {
        return Keys.hmacShaKeyFor(this.secret.getBytes());
    }

}
