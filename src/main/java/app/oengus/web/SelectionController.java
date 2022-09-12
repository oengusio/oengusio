package app.oengus.web;

import app.oengus.entity.dto.SelectionDto;
import app.oengus.entity.model.Status;
import app.oengus.service.SelectionService;
import app.oengus.spring.model.Views;
import com.fasterxml.jackson.annotation.JsonView;
import io.swagger.v3.oas.annotations.tags.Tag;
import javassist.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;

import java.time.Duration;
import java.util.List;

@CrossOrigin(maxAge = 3600)
@RestController
@RequestMapping("/v1/marathons/{marathonId}/selections")
@Tag(name = "selections-v1")
public class SelectionController {

    @Autowired
    private SelectionService selectionService;

    @GetMapping
    @PreAuthorize("isSelectionDone(#marathonId)")
    @JsonView(Views.Public.class)
    @Operation(summary = "Get all selection statuses a marathon, has a 30 minute cache"/*,
        response = SelectionDto.class*/)
    public ResponseEntity<?> findAllForMarathon(@PathVariable("marathonId") final String marathonId,
                                                @RequestParam(name = "status", required = false) final List<Status> statuses) {
        return ResponseEntity.ok()
            .cacheControl(
                CacheControl.maxAge(Duration.ofMinutes(30))
                    .cachePublic()
            )
            .body(this.selectionService.findByMarathon(marathonId, statuses));
    }

    ///////////////
    // ADMIN ROUTES

    @GetMapping("/admin")
    @PreAuthorize("(!isBanned() && canUpdateMarathon(#marathonId))")
    @JsonView(Views.Public.class)
    @Operation(hidden = true)
    public ResponseEntity<?> findAllForMarathonAdmin(@PathVariable("marathonId") final String marathonId,
                                                @RequestParam(name = "status", required = false) final List<Status> statuses) {
        return ResponseEntity.ok()
            .cacheControl(CacheControl.noCache())
            .body(this.selectionService.findByMarathon(marathonId, statuses));
    }

    @PutMapping
    @PreAuthorize("!isBanned() && canUpdateMarathon(#marathonId)")
    @Operation(hidden = true)
    public ResponseEntity<?> saveOrUpdate(@PathVariable("marathonId") final String marathonId,
                                          @RequestBody final List<SelectionDto> selections) throws NotFoundException {
        this.selectionService.saveOrUpdate(marathonId, selections);

        return ResponseEntity.noContent().build();
    }

}
