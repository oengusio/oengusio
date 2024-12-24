package app.oengus.configuration.spring.handler;

import app.oengus.adapter.security.CustomMethodSecurityExpressionRoot;
import app.oengus.application.MarathonService;
import app.oengus.application.port.persistence.PatreonStatusPersistencePort;
import app.oengus.application.port.persistence.SchedulePersistencePort;
import app.oengus.application.port.persistence.UserPersistencePort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aopalliance.intercept.MethodInvocation;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionOperations;
import org.springframework.security.authentication.AuthenticationTrustResolver;
import org.springframework.security.authentication.AuthenticationTrustResolverImpl;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.function.Supplier;

@Component
@RequiredArgsConstructor
@Slf4j
public class CustomMethodSecurityExpressionHandler extends DefaultMethodSecurityExpressionHandler {
    private final AuthenticationTrustResolver trustResolver = new AuthenticationTrustResolverImpl();

    // This prevents the logs from flooding, wtf
    // source: https://synyx.de/blog/bean-x-of-type-y-is-not-eligible-for-getting-processed-by-all-beanpostprocessors/
    private final ObjectFactory<MarathonService> marathonService;
    private final ObjectFactory<UserPersistencePort> userPersistencePort;
    private final ObjectFactory<SchedulePersistencePort> schedulePersistencePort;
    private final ObjectFactory<PatreonStatusPersistencePort> patreonStatusPersistencePort;

    @Override
    protected MethodSecurityExpressionOperations createSecurityExpressionRoot(
        final Authentication authentication, final MethodInvocation invocation) {
        log.info("=======================================");
        log.info("INITIALISED");
        log.info("=======================================");

        return getCustomMethodSecurityExpressionRoot(() -> authentication);
    }

    @Override
    public EvaluationContext createEvaluationContext(Supplier<Authentication> authentication, MethodInvocation mi) {
        log.info("=======================================");
        log.info("INITIALISED 2: electric boogaloo");
        log.info("=======================================");

        final StandardEvaluationContext context = (StandardEvaluationContext) super.createEvaluationContext(authentication, mi);
        final MethodSecurityExpressionOperations delegate = (MethodSecurityExpressionOperations) context.getRootObject().getValue();

        final var root = getCustomMethodSecurityExpressionRoot(authentication);

        context.setRootObject(root);

        return context;
    }

    @NotNull
    private CustomMethodSecurityExpressionRoot getCustomMethodSecurityExpressionRoot(Supplier<Authentication> authentication) {
        final CustomMethodSecurityExpressionRoot root = new CustomMethodSecurityExpressionRoot(
            authentication,
            this.marathonService.getObject(),
            this.userPersistencePort.getObject(),
            this.schedulePersistencePort.getObject(),
            this.patreonStatusPersistencePort.getObject()
        );

        root.setPermissionEvaluator(this.getPermissionEvaluator());
        root.setTrustResolver(this.trustResolver);
        root.setRoleHierarchy(this.getRoleHierarchy());
        return root;
    }
}
