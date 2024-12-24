package app.oengus.configuration.spring.handler;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Serializable;

public class ForbiddenHandler implements AccessDeniedHandler, Serializable {

	private static final long serialVersionUID = 1L;

	@Override
	public void handle(final HttpServletRequest request, final HttpServletResponse response,
	                   final AccessDeniedException accessDeniedException) throws IOException, ServletException {
		response.sendError(HttpServletResponse.SC_FORBIDDEN);
	}
}
