package app.oengus.adapter.security;

import app.oengus.adapter.security.dto.UserDetailsDto;
import app.oengus.domain.Role;
import io.jsonwebtoken.Claims;
import io.sentry.Sentry;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class AuthenticationFilter extends OncePerRequestFilter {
    @Autowired
    private JWTAdapter jwtPort; // TODO: See if autowire via constructor is a better option

    private static final String AUTH_HEADER = "Authorization";

    @Override
    protected void doFilterInternal(final HttpServletRequest request, @NotNull final HttpServletResponse response,
                                    @NotNull final FilterChain chain)
        throws ServletException, IOException {

        response.setHeader("Permissions-Policy", "web-share=(self \"https://oengus.fun\")");
        if (request.getHeader("Access-Control-Request-Method") != null &&
            "OPTIONS".equalsIgnoreCase(request.getMethod())) {
            response.addHeader("Access-Control-Allow-Headers", "Authorization");
            response.addHeader("Access-Control-Allow-Headers", "Content-Type, Location, Origin");
            response.addHeader("Access-Control-Max-Age", "1");
            response.addHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, PATCH");
        }


        final String authHeader = request.getHeader(AUTH_HEADER);

        // TODO: there is probably a better way of going about this
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            final String token = authHeader.substring(7);

            if (this.jwtPort.isTokenValid(token)) {
                try {
                    final Claims claims = this.jwtPort.getAllClaimsFromToken(token);
                    @SuppressWarnings("unchecked") final List<String> rolesString = claims.get("role", List.class);
                    final boolean enabled = claims.get("enabled", Boolean.class);

                    final var details = new UserDetailsDto(
                        Integer.parseInt(claims.getId()),
                        claims.getSubject(),
                        rolesString.stream().map(Role::valueOf).toList(),
                        enabled,
                        null
                    );

                    // TODO: properly test if this works as expected
                    /*Sentry.setUser(User.fromMap(Map.of(
                        "id", details.id(),
                        "username", details.username()
                    ), new SentryOptions()));*/

                    // Since we implement "UserDetails" in our DTO sentry will see the username from that.
                    final var authentication = new UsernamePasswordAuthenticationToken(
                        details,
                        null,
                        details.getAuthorities()
                    );
                    authentication.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                    );
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                } catch (final Exception e) {
                    Sentry.captureException(e);
                    //log.error("ERROR ", e);
                }
            }
        }

        if (!request.getMethod().equalsIgnoreCase("OPTIONS")) {
            chain.doFilter(request, response);
        }
    }
}
