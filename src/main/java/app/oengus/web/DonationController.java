package app.oengus.web;

import app.oengus.entity.dto.DonationStatsDto;
import app.oengus.entity.dto.OrderDto;
import app.oengus.entity.model.Donation;
import app.oengus.service.DonationService;
import app.oengus.service.ExportService;
import app.oengus.spring.model.Views;
import com.fasterxml.jackson.annotation.JsonView;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import javassist.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

@CrossOrigin(maxAge = 3600)
@RestController
@RequestMapping("/marathons/{marathonId}/donations")
@Api
public class DonationController {

    private final DonationService donationService;
    private final ExportService exportService;

    public DonationController(DonationService donationService, ExportService exportService) {
        this.donationService = donationService;
        this.exportService = exportService;
    }

    @GetMapping
    @JsonView(Views.Public.class)
    @ApiIgnore
    public ResponseEntity<?> findForMarathon(@PathVariable("marathonId") final String marathonId,
                                             @RequestParam("page") final int page,
                                             @RequestParam("size") final int size) {
        return ResponseEntity.ok()
            .cacheControl(CacheControl.maxAge(1, TimeUnit.MINUTES))
            .body(this.donationService.findForMarathon(marathonId, page, size));
    }

    @GetMapping("/stats")
    @JsonView(Views.Public.class)
    @ApiOperation(value = "Get the donation stats for a marathon, you probably want this one",
        response = DonationStatsDto.class)
    public ResponseEntity<?> findStatsForMarathon(@PathVariable("marathonId") final String marathonId) {
        return ResponseEntity.ok()
            .cacheControl(CacheControl.maxAge(1, TimeUnit.MINUTES))
            .body(this.donationService.getStats(marathonId));
    }

    @PostMapping("/donate")
    @ApiIgnore
    @PreAuthorize("!isMarathonArchived(#marathonId)")
    public ResponseEntity<?> initDonation(@PathVariable("marathonId") final String marathonId,
                                          @RequestBody final Donation donation) throws NotFoundException {
        return ResponseEntity.ok(new OrderDto(this.donationService.initDonation(marathonId, donation).id()));
    }

    @PostMapping("/validate/{code}")
    @JsonView(Views.Public.class)
    @ApiIgnore
    @PreAuthorize("!isMarathonArchived(#marathonId)")
    public ResponseEntity<?> validateDonation(@PathVariable("marathonId") final String marathonId,
                                              @PathVariable("code") final String code) {
        this.donationService.approveDonation(marathonId, code);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{code}")
    @ApiIgnore
    @PreAuthorize("!isMarathonArchived(#marathonId)")
    public ResponseEntity<?> deleteDonation(@PathVariable("marathonId") final String marathonId,
                                            @PathVariable("code") final String code) {
        this.donationService.deleteDonation(code);
        return ResponseEntity.noContent().build();
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
