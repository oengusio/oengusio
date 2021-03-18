package app.oengus.web;

import app.oengus.entity.dto.DonationStatsDto;
import app.oengus.entity.dto.OrderDto;
import app.oengus.entity.model.Donation;
import app.oengus.exception.OengusBusinessException;
import app.oengus.service.DonationService;
import app.oengus.service.ExportService;
import app.oengus.spring.model.Views;
import com.fasterxml.jackson.annotation.JsonView;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping({"/marathons/{marathonId}/donations", "/marathon/{marathonId}/donation"})
@Api(value = "/marathons/{marathonId}/donations")
public class DonationController {

    @Autowired
    private DonationService donationService;

    @Autowired
    private ExportService exportService;

    @GetMapping
    @JsonView(Views.Public.class)
    @ApiIgnore
    public ResponseEntity<?> findForMarathon(@PathVariable("marathonId") final String marathonId,
                                             @RequestParam("page") final Integer page,
                                             @RequestParam("size") final Integer size) {
        try {
            return ResponseEntity.ok()
                .cacheControl(CacheControl.maxAge(1, TimeUnit.MINUTES))
                .body(this.donationService.findForMarathon(marathonId, page, size));
        } catch (final OengusBusinessException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/stats")
    @JsonView(Views.Public.class)
    @ApiOperation(value = "Get the donation stats for a marathon, you probably want this one",
        response = DonationStatsDto.class)
    public ResponseEntity<?> findStatsForMarathon(@PathVariable("marathonId") final String marathonId) {
        try {
            return ResponseEntity.ok()
                .cacheControl(CacheControl.maxAge(1, TimeUnit.MINUTES))
                .body(this.donationService.getStats(marathonId));
        } catch (final OengusBusinessException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PostMapping("/donate")
    @ApiIgnore
    @PreAuthorize("!isMarathonArchived(#marathonId)")
    public ResponseEntity<?> initDonation(@PathVariable("marathonId") final String marathonId,
                                          @RequestBody final Donation donation) {
        try {
            return ResponseEntity.ok(new OrderDto(this.donationService.initDonation(marathonId, donation).id()));
        } catch (final OengusBusinessException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PostMapping("/validate/{code}")
    @JsonView(Views.Public.class)
    @ApiIgnore
    @PreAuthorize("!isMarathonArchived(#marathonId)")
    public ResponseEntity<?> validateDonation(@PathVariable("marathonId") final String marathonId,
                                              @PathVariable("code") final String code) {
        try {
            this.donationService.approveDonation(marathonId, code);
            return ResponseEntity.ok().build();
        } catch (final OengusBusinessException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @DeleteMapping("/{code}")
    @ApiIgnore
    @PreAuthorize("!isMarathonArchived(#marathonId)")
    public ResponseEntity<?> deleteDonation(@PathVariable("marathonId") final String marathonId,
                                            @PathVariable("code") final String code) {
        try {
            this.donationService.deleteDonation(code);
            return ResponseEntity.noContent().build();
        } catch (final OengusBusinessException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/export")
    @PreAuthorize("canUpdateMarathon(#marathonId) && !isBanned()")
    @JsonView(Views.Public.class)
    @ApiOperation(value = "Export all submitted donations by marathon to CSV")
    public void exportAllForMarathon(@PathVariable("marathonId") final String marathonId,
                                     @RequestParam("zoneId") final String zoneId,
                                     final HttpServletResponse response) throws IOException {
        response.setContentType("text/csv");
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION,
            "attachment; filename=\"" + marathonId + "-donations.csv\"");
        response.getWriter().write(this.exportService.exportDonationsToCsv(marathonId, zoneId, null).toString());
    }

}
