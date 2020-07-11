package app.oengus.spring;

import app.oengus.entity.model.User;
import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JWTUtil implements Serializable {

	private static final long serialVersionUID = 1L;

	@Value("${oengus.jwt.secret}")
	private String secret;

	@Value("${oengus.jwt.expiration}")
	private String expirationTime;

	public Claims getAllClaimsFromToken(final String token) {
		return Jwts.parser()
		           .setSigningKey(Base64.getEncoder().encodeToString(this.secret.getBytes()))
		           .parseClaimsJws(token)
		           .getBody();
	}

	private Boolean isTokenExpired(final String token) {
		try {
			this.getAllClaimsFromToken(token);
			return false;
		} catch (final ExpiredJwtException | SignatureException e) {
			return true;
		}
	}

	public String generateToken(final User user) {
		final Map<String, Object> claims = new HashMap<>();
		claims.put("role", user.getRoles());
		claims.put("enabled", user.isEnabled());
		claims.put("id", user.getId());
		return this.doGenerateToken(claims, user.getUsername(), user.getId());
	}

	private String doGenerateToken(final Map<String, Object> claims, final String username, final Integer id) {
		final Long expirationTimeLong = Long.parseLong(this.expirationTime); //in second

		final Date createdDate = new Date();
		final Date expirationDate = new Date(createdDate.getTime() + expirationTimeLong * 1000);
		return Jwts.builder()
		           .setClaims(claims)
		           .setSubject(username)
		           .setIssuedAt(createdDate)
		           .setId(Integer.toString(id))
		           .setExpiration(expirationDate)
		           .signWith(SignatureAlgorithm.HS512, Base64.getEncoder().encodeToString(this.secret.getBytes()))
		           .compact();
	}

	public Boolean validateToken(final String token) {
		return !this.isTokenExpired(token);
	}

}
