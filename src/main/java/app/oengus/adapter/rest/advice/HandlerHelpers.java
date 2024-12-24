package app.oengus.adapter.rest.advice;

import jakarta.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

public final class HandlerHelpers {
    public static Map<String, Object> toMap(final HttpServletRequest req, final Exception exception) {
        final Map<String, Object> mapper = new HashMap<>();

        mapper.put("type", exception.getClass().getSimpleName());
        mapper.put("message", exception.getMessage());
        mapper.put("method", req.getMethod());
        mapper.put("path", req.getServletPath());

        return mapper;
    }
}
