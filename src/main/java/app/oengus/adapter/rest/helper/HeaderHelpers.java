package app.oengus.adapter.rest.helper;

import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;

import java.time.Duration;
import java.util.List;

public class HeaderHelpers {

    public static HttpHeaders cachingHeaders(long minutes) {
        return cachingHeaders(minutes, true);
    }

    public static HttpHeaders cachingHeaders(long minutes, boolean varies) {
        final HttpHeaders headers = new HttpHeaders();

        headers.setCacheControl(
            CacheControl.maxAge(Duration.ofMinutes(minutes))
                .cachePublic()
                .mustRevalidate()
        );

        if (varies) {
            headers.setVary(List.of(
                "Authorization"
            ));
        }

        return headers;
    }

}
