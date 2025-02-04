package app.oengus.application.webhook.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@RequiredArgsConstructor
public class WebhookUserDto {
    private final int id;
    private String username;
    private String displayName;
    private List<String> pronouns = new ArrayList<>();
    private String country;
    private List<String> languagesSpoken = new ArrayList<>();
    private List<WebhookConnectionDto> connections = new ArrayList<>();
}
