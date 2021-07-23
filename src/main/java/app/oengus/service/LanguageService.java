package app.oengus.service;

import app.oengus.entity.dto.LanguageDto;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

@Service
public class LanguageService {
    /// <editor-fold desc="Supported languages" defaultstate="collapsed">
    private static final String[] SUPPORTED_LANGUAGES = {
        "am",
        "ar",
        "eu",
        "bn",
        "eo",
        "en-GB",
        "pt-BR",
        "bg",
        "ca",
        "chr",
        "hr",
        "cs",
        "da",
        "nl",
        "en",
        "et",
        "fil",
        "fi",
        "fr",
        "de",
        "el",
        "gu",
        "iw",
        "hi",
        "hu",
        "is",
        "id",
        "it",
        "ja",
        "kn",
        "ko",
        "lv",
        "lt",
        "ms",
        "ml",
        "mr",
        "no",
        "pl",
        "pt-PT",
        "ro",
        "ru",
        "sr",
        "zh-CN",
        "sk",
        "sl",
        "es",
        "sw",
        "sv",
        "ta",
        "te",
        "th",
        "zh-TW",
        "tr",
        "ur",
        "uk",
        "vi",
        "cy"
    };
    /// </editor-fold>

    public List<LanguageDto> searchLanguages(String search) {
        return this.searchLanguages(search, Locale.ENGLISH);
    }

    public List<LanguageDto> searchLanguages(String search, Locale clientLang) {
        final String trim = search.trim();
        final List<LanguageDto> result = new ArrayList<>();

        for (final String lang : SUPPORTED_LANGUAGES) {
            if (
                lang.contains(search) ||
                Locale.forLanguageTag(lang).getDisplayName(clientLang).toLowerCase().contains(search.toLowerCase())
            ) {
                result.add(new LanguageDto(
                    Locale.forLanguageTag(lang).getDisplayName(clientLang),
                    lang
                ));
            }
        }

        return result;
    }

    public static boolean isSupportedLanguage(String lang) {
        return Arrays.asList(SUPPORTED_LANGUAGES).contains(lang);
    }
}
