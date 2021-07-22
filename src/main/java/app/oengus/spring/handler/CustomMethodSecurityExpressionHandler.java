package app.oengus.spring.handler;

import app.oengus.service.MarathonService;
import app.oengus.service.UserService;
import app.oengus.service.repository.TeamRepositoryService;
import app.oengus.spring.security.CustomMethodSecurityExpressionRoot;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionOperations;
import org.springframework.security.authentication.AuthenticationTrustResolver;
import org.springframework.security.authentication.AuthenticationTrustResolverImpl;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component
public class CustomMethodSecurityExpressionHandler extends DefaultMethodSecurityExpressionHandler {
    private final AuthenticationTrustResolver trustResolver =
        new AuthenticationTrustResolverImpl();

    private final MarathonService marathonService;
    private final UserService userService;
    private final TeamRepositoryService teamRepositoryService;

    public CustomMethodSecurityExpressionHandler(MarathonService marathonService, UserService userService, TeamRepositoryService teamRepositoryService) {
        this.marathonService = marathonService;
        this.userService = userService;
        this.teamRepositoryService = teamRepositoryService;
    }

    @Override
    protected MethodSecurityExpressionOperations createSecurityExpressionRoot(
        final Authentication authentication, final MethodInvocation invocation) {
        final CustomMethodSecurityExpressionRoot root = new CustomMethodSecurityExpressionRoot(
            authentication,
            this.marathonService,
            this.userService,
            this.teamRepositoryService
        );
        root.setPermissionEvaluator(this.getPermissionEvaluator());
        root.setTrustResolver(this.trustResolver);
        root.setRoleHierarchy(this.getRoleHierarchy());

        return root;
    }
}
