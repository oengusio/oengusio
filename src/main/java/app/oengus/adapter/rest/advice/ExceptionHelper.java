package app.oengus.adapter.rest.advice;

import org.jspecify.annotations.NonNull;

public class ExceptionHelper {
    /**
     * Get the root cause for an exception
     *
     * @param throwable the throwable the check
     * @return A non-null throwable with the cause of the exception
     */
    @NonNull
    public static Throwable getRootCause(Throwable throwable) {
        Throwable previous = throwable;

        while (true) {
            final Throwable cause = throwable.getCause();

            if (cause == null) {
                return throwable;
            }

            throwable = cause;
        }
    }
}
