package app.oengus.adapter.rest.controller.v1;

import app.oengus.adapter.rest.mapper.SelectionDtoMapper;
import app.oengus.application.SelectionService;
import app.oengus.entity.dto.SelectionDto;
import app.oengus.entity.model.Status;
import app.oengus.spring.model.Views;
import com.fasterxml.jackson.annotation.JsonView;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import javassist.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static app.oengus.helper.HeaderHelpers.cachingHeaders;

@CrossOrigin(maxAge = 3600)
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/marathons/{marathonId}/selections")
@Tag(name = "selections-v1")
public class SelectionController {
    private final SelectionService selectionService;
    private final SelectionDtoMapper mapper;

    @GetMapping
    @PreAuthorize("isSelectionDone(#marathonId)")
    @JsonView(Views.Public.class)
    @Operation(summary = "Get all selection statuses a marathon, has a 30 minute cache"/*,
        response = SelectionDto.class*/)
    public ResponseEntity<?> findAllForMarathon(@PathVariable("marathonId") final String marathonId,
                                                @RequestParam(name = "status", required = false) final List<Status> statuses) {
        return ResponseEntity.ok()
            .headers(cachingHeaders(30, false))
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
        this.selectionService.saveSelections(
            marathonId,
            selections.stream()
                .map(this.mapper::toDomain)
                .peek((it) -> it.setMarathonId(marathonId))
                .toList()
        );

        return ResponseEntity.noContent().build();
    }

}
