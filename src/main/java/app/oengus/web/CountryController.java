package app.oengus.web;

import com.neovisionaries.i18n.CountryCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/countries")
public class CountryController {

    @GetMapping
    public ResponseEntity<?> listAllCountries() {
        List<CountryCode> output = new ArrayList<>(List.of(CountryCode.values()));

        // Remove "UNDEFINED"
        output.remove(0);
        output = output.stream()
            .filter((it) -> it.getAssignment() == CountryCode.Assignment.OFFICIALLY_ASSIGNED)
            .collect(Collectors.toList());

	    return ResponseEntity.ok(output);
    }
}
