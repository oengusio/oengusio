package app.oengus.configuration.spring.handler;

import app.oengus.adapter.security.CustomMethodSecurityExpressionRoot;
import app.oengus.application.MarathonService;
import app.oengus.application.port.persistence.UserPersistencePort;
import lombok.RequiredArgsConstructor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.beans.factory.ObjectFactory;
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

    // This prevents the logs from flooding, wtf
    // source: https://synyx.de/blog/bean-x-of-type-y-is-not-eligible-for-getting-processed-by-all-beanpostprocessors/
    private final ObjectFactory<MarathonService> marathonService;
    private final ObjectFactory<UserPersistencePort> userPersistencePort;

    @Override
    protected MethodSecurityExpressionOperations createSecurityExpressionRoot(
        final Authentication authentication, final MethodInvocation invocation) {
        final CustomMethodSecurityExpressionRoot root = new CustomMethodSecurityExpressionRoot(
            authentication,
            this.marathonService.getObject(),
            this.userPersistencePort.getObject()
        );

        root.setPermissionEvaluator(this.getPermissionEvaluator());
        root.setTrustResolver(this.trustResolver);
        root.setRoleHierarchy(this.getRoleHierarchy());

        return root;
    }
}
