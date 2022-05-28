package app.oengus.web.v2;

import app.oengus.api.PronounsPageApi;
import app.oengus.entity.model.api.Pronoun;
import app.oengus.exception.OengusBusinessException;
import app.oengus.service.LanguageService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@Tag(name = "misc-v1")
@RestController
@RequestMapping({"/v1", "/v2", ""})
public class MiscController {

    private final PronounsPageApi pronounsApi;
    private final LanguageService languageService;

    @Autowired
    public MiscController(final PronounsPageApi pronounsApi, final LanguageService languageService) {
        this.pronounsApi = pronounsApi;
        this.languageService = languageService;
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<String> bonk() {
        return ResponseEntity.ok("Bonk!");
    }

    @GetMapping("/pronouns")
    @PreAuthorize("isAuthenticated() && !isBanned()")
    public ResponseEntity<?> searchPronouns(@RequestParam final String search) {
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

    @GetMapping("/languages")
    @PreAuthorize("isAuthenticated() && !isBanned()")
    public ResponseEntity<?> searchLanguages(
        @RequestParam final String search,
        @RequestParam(value = "locale", required = false, defaultValue = "") final String locale
    ) {
        if (search.isBlank()) {
            throw new OengusBusinessException("Missing search parameter");
        }

        final Locale searchLang;

        if (locale.isBlank()) {
            searchLang = Locale.ENGLISH;
        } else {
            searchLang = Locale.forLanguageTag(locale);
        }

        try {
            // I hate how these throw instead of return null, but checking is always good
            if (searchLang.getISO3Language() == null || searchLang.getISO3Country() == null) {
                throw new OengusBusinessException("Locale is not valid");
            }
        } catch (final MissingResourceException ignored) {
            throw new OengusBusinessException("Locale is not valid");
        }

        return ResponseEntity.ok(
            this.languageService.searchLanguages(search, searchLang)
        );
    }
}
