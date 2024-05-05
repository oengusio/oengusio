package app.oengus.adapter.rest.controller.v1;

import app.oengus.adapter.rest.dto.v1.SubmissionDto;
import app.oengus.adapter.rest.OpponentRestService;
import app.oengus.adapter.rest.mapper.AnswerDtoMapper;
import app.oengus.adapter.rest.mapper.SubmissionDtoMapper;
import app.oengus.application.SubmissionService;
import app.oengus.application.port.security.UserSecurityPort;
import app.oengus.domain.submission.Submission;
import app.oengus.adapter.rest.dto.AvailabilityDto;
import app.oengus.adapter.rest.dto.misc.PageDto;
import app.oengus.adapter.rest.dto.v1.AnswerDto;
import app.oengus.application.ExportService;
import app.oengus.application.GameService;
import app.oengus.adapter.rest.Views;
import com.fasterxml.jackson.annotation.JsonView;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import javassist.NotFoundException;
import lombok.RequiredArgsConstructor;
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
import java.util.List;
import java.util.Map;

import static app.oengus.adapter.rest.helper.HeaderHelpers.cachingHeaders;

@CrossOrigin(maxAge = 3600)
@RestController
@RequestMapping("/v1/marathons/{marathonId}/submissions")
@Tag(name = "submissions-v1")
@RequiredArgsConstructor
public class SubmissionsController {
    private final SubmissionDtoMapper mapper;
    private final AnswerDtoMapper answerMapper;
    private final UserSecurityPort securityPort;
    private final GameService gameService;
    private final ExportService exportService;
    private final SubmissionService submissionService;
    private final OpponentRestService opponentRestService;

    ///////// GameController.java ////////

    @GetMapping("/export")
    @PreAuthorize("isMarathonMod(#marathonId) && !isBanned()")
    @JsonView(Views.Public.class)
    @Operation(summary = "Export all submitted games by marathon to CSV")
    public void exportAllForMarathon(@PathVariable("marathonId") final String marathonId,
                                     @RequestParam("locale") final String locale,
                                     @RequestParam("zoneId") final String zoneId,
                                     final HttpServletResponse response) throws IOException, NotFoundException {
        response.setContentType("text/csv");
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        cachingHeaders(30).toSingleValueMap().forEach(response::setHeader);
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION,
            "attachment; filename=\"" + marathonId + "-submissions.csv\"");

        try (final var writer = this.exportService.exportSubmissionsToCsv(marathonId, -1, zoneId, locale)) {
            response.getWriter().write(writer.toString());
        }
    }

    @DeleteMapping("/games/{id}")
    @PreAuthorize("canUpdateMarathon(#marathonId) && !isBanned() || isAdmin()")
    @Operation(hidden = true)
    public ResponseEntity<?> delete(@PathVariable("marathonId") final String marathonId,
                                    @PathVariable("id") final int id) throws NotFoundException {
        this.gameService.delete(id, this.securityPort.getAuthenticatedUser());

        return ResponseEntity.ok().build();
    }

    ///////// SubmissionController.java ////////

    @GetMapping
    @JsonView(Views.Public.class)
    @Operation(summary = "Find all submissions by marathon, has a 30 minute cache")
    public ResponseEntity<PageDto<SubmissionDto>> findAllSubmissions(
        @PathVariable("marathonId") final String marathonId,
        @RequestParam(value = "page", required = false, defaultValue = "1") final int page
    ) {
        final var foundSubmissions = this.submissionService.findByMarathonNew(marathonId, Math.max(0, page - 1));

        // Strip code from response here
        foundSubmissions.forEach((submission) -> {
            submission.getGames().forEach((game) -> {
                game.getCategories().forEach((category) -> {
                    category.setCode(null);
                });
            });
        });

        final var res = foundSubmissions.map(this.mapper::toV1Dto);

        this.opponentRestService.setCategoryAndGameNameOnOpponents(res, marathonId);

        return ResponseEntity.ok()
            .headers(cachingHeaders(30, false))
            .body(new PageDto<>(res));
    }

    @GetMapping("/search")
    @JsonView(Views.Public.class)
    @Operation(summary = "Search in submissions, has a 30 minute cache on the result")
    public ResponseEntity<PageDto<SubmissionDto>> serachForSubmissions(
        @PathVariable("marathonId") final String marathonId,
        @RequestParam(value = "q") final String q,
        @RequestParam(value = "status", required = false) String status,
        @RequestParam(value = "page", required = false, defaultValue = "1") final int page
    ) {
        final String nullableStatus = ValueConstants.DEFAULT_NONE.equals(status) ? null : status;
        final var foundSubmissions = this.submissionService.searchForMarathon(
            marathonId, q, nullableStatus, Math.max(0, page - 1)
        );

        // Strip code from response here
        foundSubmissions.forEach((submission) -> {
            submission.getGames().forEach((game) -> {
                game.getCategories().forEach((category) -> {
                    category.setCode(null);
                });
            });
        });

        final var res = foundSubmissions.map(this.mapper::toV1Dto);

        this.opponentRestService.setCategoryAndGameNameOnOpponents(res, marathonId);

        return ResponseEntity.ok()
            .headers(cachingHeaders(30, false))
            .body(new PageDto<>(res));
    }

    @GetMapping("/answers")
    @JsonView(Views.Public.class)
    @Operation(summary = "Get the answers for this marathon")
    @PreAuthorize("!isBanned() && canUpdateMarathon(#marathonId)")
    public ResponseEntity<List<AnswerDto>> findAllAnswers(@PathVariable("marathonId") final String marathonId) {
        final var answers = this.submissionService.findAnswersByMarathon(marathonId);

        return ResponseEntity.ok()
            .cacheControl(CacheControl.noCache())
            .body(
                answers.stream().map(this.answerMapper::fromDomain).toList()
            );
    }

    @PostMapping
    @RolesAllowed({"ROLE_USER"})
    @PreAuthorize("isAuthenticated() && !isBanned() && areSubmissionsOpen(#marathonId)")
    @Operation(hidden = true)
    public ResponseEntity<?> create(@RequestBody @Valid final SubmissionDto submission,
                                    @PathVariable("marathonId") final String marathonId,
                                    final BindingResult bindingResult) throws NotFoundException {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(bindingResult.getAllErrors());
        }

        final var user = this.securityPort.getAuthenticatedUser();

        if (user == null) {
            return ResponseEntity.badRequest().build();
        }

        // TODO: submission update dto
        this.submissionService.save(this.mapper.fromV1Dto(submission, marathonId), user, marathonId);

        return ResponseEntity.created(URI.create("/marathon/" + marathonId + "/submissions/me")).build();
    }

    @PutMapping
    @RolesAllowed({"ROLE_USER"})
    @PreAuthorize(value = "!isBanned() && canEditSubmissions(#marathonId) " +
        "&& #submission != null " +
        "&& #submission.id != null " +
        "&& (isSelf(#submission.user.id) || isAdmin())")
    @Operation(hidden = true)
    public ResponseEntity<?> update(@RequestBody @Valid final SubmissionDto submission,
                                    @PathVariable("marathonId") final String marathonId,
                                    final BindingResult bindingResult) throws NotFoundException {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(bindingResult.getAllErrors());
        }

        final var user = this.securityPort.getAuthenticatedUser();

        this.submissionService.update(this.mapper.fromV1Dto(submission, marathonId), user, marathonId);

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
    public ResponseEntity<Map<String, List<AvailabilityDto>>> getAvailabilitiesForUser(
        @PathVariable("marathonId") final String marathonId,
        @PathVariable("userId") final int userId
    ) {
        return ResponseEntity.ok(this.submissionService.getRunnerAvailabilitiesForMarathon(marathonId, userId));
    }

    @GetMapping("/me")
    @RolesAllowed({"ROLE_USER"})
    @PreAuthorize("isAuthenticated() && !isBanned()")
    @JsonView(Views.Internal.class)
    @Operation(hidden = true)
    public ResponseEntity<SubmissionDto> getMySubmission(@PathVariable("marathonId") final String marathonId) {
        final var userId = this.securityPort.getAuthenticatedUserId();
        final Submission submission = this.submissionService.findByUserAndMarathon(userId, marathonId);

        if (submission == null) {
            return ResponseEntity.notFound()
                .cacheControl(CacheControl.noCache())
                .build();
        }

        final var res = this.mapper.toV1Dto(submission);

        this.opponentRestService.setCategoryAndGameNameOnOpponents(res, marathonId);

        return ResponseEntity.ok()
            .cacheControl(CacheControl.noCache())
            .body(res);
    }

    @DeleteMapping("/{id}")
    @RolesAllowed({"ROLE_USER"})
    @PreAuthorize("isAuthenticated() && !isBanned()")
    @Operation(hidden = true)
    public ResponseEntity<?> delete(@PathVariable("id") final int id) throws NotFoundException {
        this.submissionService.delete(id, this.securityPort.getAuthenticatedUser());

        return ResponseEntity.ok().build();
    }

}
