package app.oengus.adapter.rest.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static app.oengus.adapter.rest.helper.HeaderHelpers.cachingHeaders;

@RestController
@Tag(name = "root")
@RequestMapping("/")
@CrossOrigin(maxAge = 3600)
public class RootController {

    // Redirect snoopers to the documentation
    @GetMapping
    @Operation(hidden = true)
    public ResponseEntity<String> index() {
        return ResponseEntity.ok()
            .headers(cachingHeaders(60, false))
            .contentType(MediaType.TEXT_HTML)
            .body("Go visit <a href=\"https://docs.oengus.io/\" target=\"_blank\">https://docs.oengus.io/</a> ;)");
    }
}
