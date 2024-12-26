package app.oengus.adapter.rest.controller.v1;

import app.oengus.adapter.rest.dto.OrderDto;
import app.oengus.adapter.jpa.entity.Donation;
import app.oengus.application.DonationService;
import app.oengus.application.ExportService;
import app.oengus.application.MarathonService;
import app.oengus.adapter.rest.Views;
import com.fasterxml.jackson.annotation.JsonView;
import io.swagger.v3.oas.annotations.tags.Tag;
import javassist.NotFoundException;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

@CrossOrigin(maxAge = 3600)
@RestController
@RequestMapping("/v1/marathons/{marathonId}/donations")
@Tag(name = "donations-v1")
public class DonationController {

    private final MarathonService marathonService;
    private final DonationService donationService;
    private final ExportService exportService;

    public DonationController(MarathonService marathonService, DonationService donationService, ExportService exportService) {
        this.marathonService = marathonService;
        this.donationService = donationService;
        this.exportService = exportService;
    }

    @GetMapping
    @JsonView(Views.Public.class)
    @Operation(hidden = true)
    public ResponseEntity<?> findForMarathon(@PathVariable("marathonId") final String marathonId,
                                             @RequestParam("page") final int page,
                                             @RequestParam("size") final int size) {
        return ResponseEntity.ok()
            .cacheControl(CacheControl.noCache())
            .body(this.donationService.findForMarathon(marathonId, page, size));
    }

    @GetMapping("/stats")
    @JsonView(Views.Public.class)
    @Operation(summary = "Get the donation stats for a marathon, you probably want this one"/*,
        response = DonationStatsDto.class*/)
    public ResponseEntity<?> findStatsForMarathon(@PathVariable("marathonId") final String marathonId) throws NotFoundException {
        if (!this.marathonService.exists(marathonId)) {
            throw new NotFoundException("Marathon not found");
        }

        return ResponseEntity.ok()
            .cacheControl(CacheControl.noCache())
            .body(this.donationService.getStats(marathonId));
    }

    @PostMapping("/donate")
    @Operation(hidden = true)
    @PreAuthorize("!isMarathonArchived(#marathonId)")
    public ResponseEntity<?> initDonation(@PathVariable("marathonId") final String marathonId,
                                          @RequestBody final Donation donation) throws NotFoundException {
        return ResponseEntity.ok(new OrderDto(this.donationService.initDonation(marathonId, donation).id()));
    }

    @PostMapping("/validate/{code}")
    @JsonView(Views.Public.class)
    @Operation(hidden = true)
    @PreAuthorize("!isMarathonArchived(#marathonId)")
    public ResponseEntity<?> validateDonation(@PathVariable("marathonId") final String marathonId,
                                              @PathVariable("code") final String code) {
        this.donationService.approveDonation(marathonId, code);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{code}")
    @Operation(hidden = true)
    @PreAuthorize("!isMarathonArchived(#marathonId)")
    public ResponseEntity<?> deleteDonation(@PathVariable("marathonId") final String marathonId,
                                            @PathVariable("code") final String code) {
        this.donationService.deleteDonation(code);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/export")
    @PreAuthorize("canUpdateMarathon(#marathonId) && !isBanned()")
    @JsonView(Views.Public.class)
    @Operation(summary = "Export all submitted donations by marathon to CSV")
    public void exportAllForMarathon(@PathVariable("marathonId") final String marathonId,
                                     @RequestParam("zoneId") final String zoneId,
                                     final HttpServletResponse response) throws IOException {
        response.setContentType("text/csv");
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION,
            "attachment; filename=\"" + marathonId + "-donations.csv\"");
        response.getWriter().write(this.exportService.exportDonationsToCsv(marathonId, -1, zoneId, null).toString());
    }

}
