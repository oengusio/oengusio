package app.oengus.domain;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Connection {
    private int id;
    private SocialPlatform platform;
    private String username;
}
