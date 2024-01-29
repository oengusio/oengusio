package app.oengus.spring;

import app.oengus.spring.handler.ForbiddenHandler;
import app.oengus.spring.handler.UnauthorizedHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class WebSecurityConfiguration {

	@Bean
	public UnauthorizedHandler unauthorizedHandler() {
		return new UnauthorizedHandler();
	}

	@Bean
	public ForbiddenHandler forbiddenHandler() {
		return new ForbiddenHandler();
	}

	@Bean
	public AuthenticationFilter authenticationFilterBean() {
		return new AuthenticationFilter();
	}

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(10);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
            // we don't need CSRF because our token is invulnerable
            .csrf(AbstractHttpConfigurer::disable)

            .exceptionHandling((handler) ->
                handler.authenticationEntryPoint(this.unauthorizedHandler())
                       .accessDeniedHandler(this.forbiddenHandler())
            )

            // don't create session
            .sessionManagement(
                (manager) -> manager.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )

            // custom JWT based security filter
            .addFilterBefore(this.authenticationFilterBean(), UsernamePasswordAuthenticationFilter.class)

            // enable page caching
            .headers(HeadersConfigurer::cacheControl)

            .build();
    }
}
