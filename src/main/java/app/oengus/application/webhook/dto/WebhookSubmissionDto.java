package app.oengus.application.webhook.dto;

import app.oengus.domain.submission.Game;
import app.oengus.domain.submission.Opponent;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@RequiredArgsConstructor
public class WebhookSubmissionDto {
    private final int id;
    private final String marathonId;

    private WebhookUserDto user;

    private List<Opponent> opponents = new ArrayList<>();
    private Set<Game> games = new HashSet<>();
}
