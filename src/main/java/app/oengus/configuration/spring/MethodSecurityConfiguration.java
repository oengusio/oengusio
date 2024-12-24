package app.oengus.configuration.spring;

import app.oengus.configuration.spring.handler.CustomMethodSecurityExpressionHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.GlobalMethodSecurityConfiguration;

@Configuration
@RequiredArgsConstructor
@EnableMethodSecurity(jsr250Enabled = true)
//@EnableGlobalMethodSecurity(prePostEnabled = true, jsr250Enabled = true) // TODO: fix
// TODO: replace extends with PrePostMethodSecurityConfiguration
public class MethodSecurityConfiguration /*extends GlobalMethodSecurityConfiguration*/ {
//    private final CustomMethodSecurityExpressionHandler customMethodSecurityExpressionHandler;
//
//	@Bean
//	public MethodSecurityExpressionHandler createExpressionHandler() {
//		return this.customMethodSecurityExpressionHandler;
//	}
}
