package app.oengus.adapter.rest.controller.v2;

import app.oengus.application.api.PronounsPageApi;
import app.oengus.domain.api.Pronoun;
import app.oengus.adapter.rest.helper.HeaderHelpers;
import app.oengus.application.LanguageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

import static app.oengus.adapter.rest.helper.HeaderHelpers.cachingHeaders;

@CrossOrigin(maxAge = 3600)
@Tag(name = "misc-v1")
@RestController
@RequestMapping({"/v1", "/v2"})
@RequiredArgsConstructor
public class MiscController {
    private final PronounsPageApi pronounsApi;
    private final LanguageService languageService;

    @GetMapping
    // @PreAuthorize("isAuthenticated()")
    public ResponseEntity<String> bonk() throws Exception {
        return ResponseEntity.ok()
            .headers(cachingHeaders(30, false))
            .body("bonk!");
    }

    @GetMapping("/pronouns")
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
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Missing search parameter");
        }

        // TODO: can I put this in the client?
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

        // Cache for five minutes to not spam the api.
        return ResponseEntity
            .status(HttpStatus.OK)
            .headers(HeaderHelpers.cachingHeaders(5))
            .body(response);
    }

    @GetMapping("/languages")
    public ResponseEntity<?> searchLanguages(
        @RequestParam final String search,
        @RequestParam(value = "locale", required = false, defaultValue = "") final String locale
    ) {
        if (search.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Missing search parameter");
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
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Locale is not valid");
            }
        } catch (final MissingResourceException ignored) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Locale is not valid");
        }

        return ResponseEntity
            .status(HttpStatus.OK)
            .headers(HeaderHelpers.cachingHeaders(5))
            .body(
                this.languageService.searchLanguages(search, searchLang)
            );
    }
}
