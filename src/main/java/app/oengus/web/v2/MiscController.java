package app.oengus.web.v2;

import app.oengus.api.PronounsPageApi;
import app.oengus.entity.model.api.Pronoun;
import app.oengus.exception.OengusBusinessException;
import app.oengus.service.LanguageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
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
@RequestMapping({"/v1", "/v2"})
public class MiscController {

    private final PronounsPageApi pronounsApi;
    private final LanguageService languageService;

    @Autowired
    public MiscController(final PronounsPageApi pronounsApi, final LanguageService languageService) {
        this.pronounsApi = pronounsApi;
        this.languageService = languageService;
    }

    @GetMapping
    // @PreAuthorize("isAuthenticated()")
    public ResponseEntity<String> bonk() throws InterruptedException {
        return ResponseEntity.ok("(:");
    }

    @GetMapping("/pronouns")
    @PreAuthorize("isAuthenticated() && !isBanned()")
    @Operation(
        summary = "Search pronouns from the pronouns.page api",
        responses = {
            @ApiResponse(
                description = "List of pronouns that match your search",
                responseCode = "200",
                content = @Content(
                    mediaType = "application/json",
                    array = @ArraySchema(schema = @Schema(implementation = String.class))
                )
            )
        }
    )
    public ResponseEntity<List<String>> searchPronouns(@RequestParam final String search) {
        if (search.isBlank()) {
            throw new OengusBusinessException("Missing search parameter");
        }

        final String lower = search.toLowerCase().trim();
        final Map<String, Pronoun> pronouns = this.pronounsApi.getPronouns();
        final List<String> response = pronouns.entrySet()
            .stream()
            .filter((entry) -> {
                final Pronoun value = entry.getValue();

                return entry.getKey().contains(lower)||
                    value.getCanonicalName().contains(lower) || (
                    value.getAliases().length > 0 && Arrays.stream(value.getAliases()).anyMatch((s) -> s.contains(lower))
                );
            })
            .map(Map.Entry::getValue)
            .map(Pronoun::getEffectiveName)
            .toList();

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
