package app.oengus.adapter.security;

import app.oengus.application.port.security.JWTPort;
import app.oengus.domain.OengusUser;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import io.sentry.Hint;
import io.sentry.Sentry;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Date;
import java.util.Map;
import java.util.NoSuchElementException;

@Component
public class JWTAdapter implements JWTPort {
    private static final String JWT_ISS = "OengusIO";

    @Value("${oengus.jwt.secret}")
    private String secret;

    @Value("${oengus.jwt.expiration}") // 604800 seconds == 7 days
    private String expirationTime;

    @Override
    public Claims getAllClaimsFromToken(final String token) {
        return Jwts.parser()
            .verifyWith(getKey())
            .requireIssuer(JWT_ISS)
            .build()
            .parseSignedClaims(token)
            .getPayload();
    }

    @Override
    public boolean isTokenValid(final String token) {
        try {
            final Claims claims = this.getAllClaimsFromToken(token);

            return new Date().before(claims.getExpiration());
//            return true;
        } catch (final SecurityException | JwtException e) {
            return false;
        } catch (final NoSuchElementException nse) {
            final var hint = new Hint();

            hint.set("tokenNull", token == null);
            hint.set("tokenBlank", StringUtils.isBlank(token));
            hint.set("token", token);

            // Send the token to sentry
            // I think the token might be fucked in BACKEND-7T
            Sentry.captureException(nse, hint);

            return false;
        }
    }

    @Override
    public String generateToken(final OengusUser user) {
        final var claims = Map.of(
            "role", user.getRoles(),
            "enabled", user.isEnabled(),
            "id", user.getId()
        );

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
