package app.oengus.web;

import app.oengus.api.PronounsPageApi;
import app.oengus.entity.model.api.Pronoun;
import app.oengus.exception.OengusBusinessException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping
public class MiscController {

    private final PronounsPageApi pronounsApi;

    @Autowired
    public MiscController(final PronounsPageApi pronounsApi) {
        this.pronounsApi = pronounsApi;
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> bonk() {
        return ResponseEntity.ok("Bonk!");
    }

    @GetMapping("/pronouns")
    @PreAuthorize("!isBanned()")
    public ResponseEntity<?> searchPronouns(@RequestParam String search) {
        if (search.isBlank()) {
            throw new OengusBusinessException("Missing search parameter");
        }

        final String lower = search.toLowerCase().trim();
        final List<String> response = new ArrayList<>();
        final Map<String, Pronoun> pronouns = this.pronounsApi.getPronouns();

        for (final Map.Entry<String, Pronoun> entry : pronouns.entrySet()) {
            final Pronoun value = entry.getValue();

            if (
                entry.getKey().contains(lower) ||
                    value.getCanonicalName().contains(lower) || (
                    value.getAliases().length > 0 && Arrays.stream(value.getAliases()).anyMatch((s) -> s.contains(lower))
                )
            ) {
                response.add(value.getEffectiveName());
            }
        }

        return ResponseEntity.ok(response);
    }

}
