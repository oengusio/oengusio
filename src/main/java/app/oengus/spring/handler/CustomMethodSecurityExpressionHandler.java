package app.oengus.spring.handler;

import app.oengus.application.MarathonService;
import app.oengus.application.port.persistence.UserPersistencePort;
import app.oengus.spring.security.CustomMethodSecurityExpressionRoot;
import lombok.RequiredArgsConstructor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionOperations;
import org.springframework.security.authentication.AuthenticationTrustResolver;
import org.springframework.security.authentication.AuthenticationTrustResolverImpl;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CustomMethodSecurityExpressionHandler extends DefaultMethodSecurityExpressionHandler {
    private final AuthenticationTrustResolver trustResolver = new AuthenticationTrustResolverImpl();

    private final MarathonService marathonService;
    private final UserPersistencePort userPersistencePort;

    @Override
    protected MethodSecurityExpressionOperations createSecurityExpressionRoot(
        final Authentication authentication, final MethodInvocation invocation) {
        final CustomMethodSecurityExpressionRoot root = new CustomMethodSecurityExpressionRoot(
            authentication,
            this.marathonService,
            this.userPersistencePort
        );

        root.setPermissionEvaluator(this.getPermissionEvaluator());
        root.setTrustResolver(this.trustResolver);
        root.setRoleHierarchy(this.getRoleHierarchy());

        return root;
    }
}
