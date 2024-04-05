package app.oengus.adapter.rest.controller.v1;

import app.oengus.entity.model.Incentive;
import app.oengus.service.IncentiveService;
import app.oengus.spring.model.Views;
import com.fasterxml.jackson.annotation.JsonView;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import javassist.NotFoundException;
import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import java.util.List;

@CrossOrigin(maxAge = 3600)
@RestController
@RequestMapping("/v1/marathons/{marathonId}/incentives")
@Tag(name = "incentives-v1")
public class IncentiveController {

    private final IncentiveService incentiveService;

    public IncentiveController(IncentiveService incentiveService) {this.incentiveService = incentiveService;}

    @GetMapping
    @JsonView(Views.Public.class)
    @Operation(summary = "Get all incentives for a marathon"/*,
        response = Incentive.class,
        responseContainer = "List"*/)
    public ResponseEntity<?> findAllForMarathon(@PathVariable("marathonId") final String marathonId,
                                                @RequestParam(required = false, defaultValue = "true") final boolean withLocked,
                                                @RequestParam(required = false, defaultValue = "false") final boolean withUnapproved) throws NotFoundException {
        return ResponseEntity.ok()
            .cacheControl(CacheControl.noCache())
            .body(this.incentiveService.findByMarathon(marathonId, withLocked, withUnapproved));
    }

    @PostMapping
    @JsonView(Views.Public.class)
    @RolesAllowed({"ROLE_USER"})
    @PreAuthorize("canUpdateMarathon(#marathonId) && !isBanned()")
    @Operation(hidden = true)
    public ResponseEntity<?> save(@PathVariable("marathonId") final String marathonId,
                                  @RequestBody final List<Incentive> incentives) {
        return ResponseEntity.ok(this.incentiveService.saveAll(incentives, marathonId));
    }

}
