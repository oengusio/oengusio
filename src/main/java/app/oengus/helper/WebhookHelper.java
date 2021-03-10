package app.oengus.helper;

import java.util.HashMap;
import java.util.Map;

public class WebhookHelper {
    // casting galore :D
    public static Map<String, Object> createParameters(Object... args) {
        if (args.length % 2 != 0) {
            throw new IllegalArgumentException("Args must be in pair formation");
        }

        final Map<String, Object> params = new HashMap<>();

        for (int i = 0; i < args.length; i += 2) {
            params.put((String) args[i], args[i + 1]);
        }

        return params;
    }
}
