package app.oengus.web.v1;

import app.oengus.entity.dto.misc.PageDto;
import app.oengus.entity.dto.v1.answers.AnswerDto;
import app.oengus.entity.dto.v1.submissions.SubmissionDto;
import app.oengus.entity.model.Submission;
import app.oengus.helper.PrincipalHelper;
import app.oengus.service.ExportService;
import app.oengus.service.GameService;
import app.oengus.service.SubmissionService;
import app.oengus.spring.model.Views;
import com.fasterxml.jackson.annotation.JsonView;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import javassist.NotFoundException;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.security.Principal;
import java.util.List;

import static app.oengus.helper.HeaderHelpers.cachingHeaders;

@CrossOrigin(maxAge = 3600)
@RestController
@RequestMapping("/v1/marathons/{marathonId}/submissions")
@Tag(name = "submissions-v1")
public class SubmissionsController {

    private final GameService gameService;
    private final ExportService exportService;
    private final SubmissionService submissionService;

    public SubmissionsController(GameService gameService, ExportService exportService, SubmissionService submissionService) {
        this.gameService = gameService;
        this.exportService = exportService;
        this.submissionService = submissionService;
    }

    ///////// GameController.java ////////

    @GetMapping("/export")
    @PreAuthorize("canUpdateMarathon(#marathonId) && !isBanned()")
    @JsonView(Views.Public.class)
    @Operation(summary = "Export all submitted games by marathon to CSV")
    public void exportAllForMarathon(@PathVariable("marathonId") final String marathonId,
                                     @RequestParam("locale") final String locale,
                                     @RequestParam("zoneId") final String zoneId,
                                     final HttpServletResponse response) throws IOException {
        response.setContentType("text/csv");
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        cachingHeaders(30).toSingleValueMap().forEach(response::setHeader);
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION,
            "attachment; filename=\"" + marathonId + "-submissions.csv\"");
        response.getWriter().write(this.exportService.exportSubmissionsToCsv(marathonId, zoneId, locale).toString());
    }

    @DeleteMapping("/games/{id}")
    @PreAuthorize("canUpdateMarathon(#marathonId) && !isBanned() || isAdmin()")
    @Operation(hidden = true)
    public ResponseEntity<?> delete(@PathVariable("marathonId") final String marathonId,
                                    @PathVariable("id") final int id, final Principal principal) throws NotFoundException {
        this.gameService.delete(id, PrincipalHelper.getUserFromPrincipal(principal));

        return ResponseEntity.ok().build();
    }

    ///////// SubmissionController.java ////////

    @GetMapping
    @JsonView(Views.Public.class)
    @Operation(summary = "Find all submissions by marathon, has a 30 minute cache")
    public ResponseEntity<PageDto<SubmissionDto>> findAllSubmissions(
        @PathVariable("marathonId") final String marathonId,
        @RequestParam(value = "page", required = false, defaultValue = "1") final int page
    ) throws NotFoundException {
        return ResponseEntity.ok()
            .headers(cachingHeaders(30, false))
            .body(this.submissionService.findByMarathonNew(marathonId, Math.max(0, page - 1)));
    }

    @GetMapping("/search")
    @JsonView(Views.Public.class)
    @Operation(summary = "Search in submissions, has a 30 minute cache on the result")
    public ResponseEntity<List<SubmissionDto>> serachForSubmissions(
        @PathVariable("marathonId") final String marathonId,
        @RequestParam(value = "q") final String q,
        @RequestParam(value = "status", required = false) String status
    ) {
        final String nullableStatus = ValueConstants.DEFAULT_NONE.equals(status) ? null : status;

        return ResponseEntity.ok()
            .headers(cachingHeaders(30, false))
            .body(this.submissionService.searchForMarathon(marathonId, q, nullableStatus));
    }

    @GetMapping("/answers")
    @JsonView(Views.Public.class)
    @Operation(summary = "Get the answers for this marathon")
    @PreAuthorize("!isBanned() && canUpdateMarathon(#marathonId)")
    public ResponseEntity<List<AnswerDto>> findAllAnswers(@PathVariable("marathonId") final String marathonId) {
        return ResponseEntity.ok()
            .cacheControl(CacheControl.noCache())
            .body(this.submissionService.findAnswersByMarathon(marathonId));
    }

    @PostMapping
    @RolesAllowed({"ROLE_USER"})
    @PreAuthorize("!isBanned() && areSubmissionsOpen(#marathonId)")
    @Operation(hidden = true)
    public ResponseEntity<?> create(@RequestBody @Valid final Submission submission,
                                    @PathVariable("marathonId") final String marathonId,
                                    final Principal principal,
                                    final BindingResult bindingResult) throws NotFoundException {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(bindingResult.getAllErrors());
        }

        if (principal == null) {
            return ResponseEntity.badRequest().build();
        }

        this.submissionService.save(submission,
            PrincipalHelper.getUserFromPrincipal(principal),
            marathonId);

        return ResponseEntity.created(URI.create("/marathon/" + marathonId + "/submissions/me")).build();
    }

    @PutMapping
    @RolesAllowed({"ROLE_USER"})
    @PreAuthorize(value = "!isBanned() && canEditSubmissions(#marathonId) " +
        "&& #submission.id != null " +
        "&& (isSelf(#submission.user.id) || isAdmin())")
    @Operation(hidden = true)
    public ResponseEntity<?> update(@RequestBody @Valid final Submission submission,
                                    @PathVariable("marathonId") final String marathonId,
                                    final Principal principal,
                                    final BindingResult bindingResult) throws NotFoundException {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(bindingResult.getAllErrors());
        }

        this.submissionService.update(submission,
            PrincipalHelper.getUserFromPrincipal(principal),
            marathonId);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/availabilities")
    @PreAuthorize("!isBanned() && canUpdateMarathon(#marathonId)")
    @Operation(hidden = true)
    public ResponseEntity<?> getAvailabilities(@PathVariable("marathonId") final String marathonId) {
        return ResponseEntity.ok()
            .cacheControl(CacheControl.noCache())
            .body(this.submissionService.getRunnersAvailabilitiesForMarathon(marathonId));
    }

    @GetMapping("/availabilities/{userId}")
    @PreAuthorize("!isBanned() && canUpdateMarathon(#marathonId)")
    @Operation(hidden = true)
    public ResponseEntity<?> getAvailabilitiesForUser(@PathVariable("marathonId") final String marathonId,
                                                      @PathVariable("userId") final int userId) throws NotFoundException {
        return ResponseEntity.ok(this.submissionService.getRunnerAvailabilitiesForMarathon(marathonId, userId));
    }

    @GetMapping("/me")
    @RolesAllowed({"ROLE_USER"})
    @JsonView(Views.Public.class)
    @Operation(hidden = true)
    public ResponseEntity<Submission> getMySubmission(@PathVariable("marathonId") final String marathonId, final Principal principal) {
        final Submission submission = this.submissionService.findByUserAndMarathon(
            PrincipalHelper.getUserFromPrincipal(principal),
            marathonId
        );

        return ResponseEntity.ok()
            .cacheControl(CacheControl.noCache())
            .body(submission);
    }

    @DeleteMapping("/{id}")
    @RolesAllowed({"ROLE_USER"})
    @PreAuthorize(value = "!isBanned()")
    @Operation(hidden = true)
    public ResponseEntity<?> delete(@PathVariable("id") final int id, final Principal principal) throws NotFoundException {
        this.submissionService.delete(id, PrincipalHelper.getUserFromPrincipal(principal));

        return ResponseEntity.ok().build();
    }

}
