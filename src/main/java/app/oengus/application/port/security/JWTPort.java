package app.oengus.application.port.security;

import app.oengus.domain.OengusUser;
import io.jsonwebtoken.Claims;

public interface JWTPort {
    Claims getAllClaimsFromToken(String token);

    boolean isTokenValid(String token);

    String generateToken(final OengusUser user);
}
