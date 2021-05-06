package app.oengus.web;

import app.oengus.entity.model.Submission;
import app.oengus.exception.SubmissionsClosedException;
import app.oengus.helper.PrincipalHelper;
import app.oengus.service.ExportService;
import app.oengus.service.GameService;
import app.oengus.service.SubmissionService;
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
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.annotation.security.RolesAllowed;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.security.Principal;
import java.util.concurrent.TimeUnit;

@CrossOrigin
@RestController
@RequestMapping("/marathons/{marathonId}/submissions")
@Api
public class SubmissionsController {

    @Autowired
    private GameService gameService;

    @Autowired
    private ExportService exportService;

    @Autowired
    private SubmissionService submissionService;

    ///////// GameController.java ////////

    @GetMapping("/export")
    @PreAuthorize("canUpdateMarathon(#marathonId) && !isBanned()")
    @JsonView(Views.Public.class)
    @ApiOperation(value = "Export all submitted games by marathon to CSV")
    public void exportAllForMarathon(@PathVariable("marathonId") final String marathonId,
                                     @RequestParam("locale") final String locale,
                                     @RequestParam("zoneId") final String zoneId,
                                     final HttpServletResponse response) throws IOException {
        response.setContentType("text/csv");
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION,
            "attachment; filename=\"" + marathonId + "-submissions.csv\"");
        response.getWriter().write(this.exportService.exportSubmissionsToCsv(marathonId, zoneId, locale).toString());
    }

    @DeleteMapping("/games/{id}")
    @PreAuthorize("canUpdateMarathon(#marathonId) && !isBanned() || isAdmin()")
    @ApiIgnore
    public ResponseEntity<?> delete(@PathVariable("marathonId") final String marathonId,
                                    @PathVariable("id") final int id, final Principal principal) {
        try {
            this.gameService.delete(id, PrincipalHelper.getUserFromPrincipal(principal));

            return ResponseEntity.ok().build();
        } catch (NotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    ///////// SubmissionController.java ////////

    @GetMapping
    @JsonView(Views.Public.class)
    @ApiOperation(value = "Find all submissions by marathon",
        response = Submission.class,
        responseContainer = "List")
    public ResponseEntity<?> findAllSubmissions(@PathVariable("marathonId") final String marathonId) {
        return ResponseEntity.ok()
            .cacheControl(CacheControl.maxAge(1, TimeUnit.MINUTES))
            .body(this.submissionService.findByMarathon(marathonId));
    }

    @PostMapping
    @RolesAllowed({"ROLE_USER"})
    @PreAuthorize("!isBanned() && areSubmissionsOpen(#marathonId)")
    @ApiIgnore
    public ResponseEntity<?> create(@RequestBody @Valid final Submission submission,
                                    @PathVariable("marathonId") final String marathonId,
                                    final Principal principal,
                                    final BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(bindingResult.getAllErrors());
        }

        if (principal == null) {
            return ResponseEntity.badRequest().build();
        }

        try {
            this.submissionService.save(submission,
                PrincipalHelper.getUserFromPrincipal(principal),
                marathonId);
            return ResponseEntity.created(URI.create("/marathon/" + marathonId + "/submissions/me")).build();
        } catch (final NotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (final SubmissionsClosedException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping
    @RolesAllowed({"ROLE_USER"})
    @PreAuthorize(value = "!isBanned() && areSubmissionsOpen(#marathonId) " +
        "&& #submission.id != null " +
        "&& (#submission.user.id == principal.id || isAdmin())")
    @ApiIgnore
    public ResponseEntity<?> update(@RequestBody @Valid final Submission submission,
                                    @PathVariable("marathonId") final String marathonId,
                                    final Principal principal,
                                    final BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(bindingResult.getAllErrors());
        }
        try {
            this.submissionService.update(submission,
                PrincipalHelper.getUserFromPrincipal(principal),
                marathonId);
            return ResponseEntity.noContent().build();
        } catch (final NotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (final SubmissionsClosedException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/availabilities")
    @PreAuthorize("!isBanned() && canUpdateMarathon(#marathonId)")
    @ApiIgnore
    public ResponseEntity<?> getAvailabilities(@PathVariable("marathonId") final String marathonId) {
        return ResponseEntity.ok()
            .cacheControl(CacheControl.maxAge(1, TimeUnit.MINUTES))
            .body(this.submissionService.getRunnersAvailabilitiesForMarathon(marathonId));
    }

    @GetMapping("/answers")
    @PreAuthorize("!isBanned() && canUpdateMarathon(#marathonId)")
    @ApiIgnore
    public ResponseEntity<?> getAnswers(@PathVariable("marathonId") final String marathonId) {
        return ResponseEntity.ok()
            .cacheControl(CacheControl.maxAge(1, TimeUnit.MINUTES))
            .body(this.submissionService.findCustomAnswersByMarathon(marathonId));
    }

    @GetMapping("/availabilities/{userId}")
    @PreAuthorize("!isBanned() && canUpdateMarathon(#marathonId)")
    @ApiIgnore
    public ResponseEntity<?> getAvailabilitiesForUser(@PathVariable("marathonId") final String marathonId,
                                                      @PathVariable("userId") final int userId) {
        try {
            return ResponseEntity.ok(this.submissionService.getRunnerAvailabilitiesForMarathon(marathonId, userId));
        } catch (final NotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/me")
    @RolesAllowed({"ROLE_USER"})
    @JsonView(Views.Public.class)
    @ApiIgnore
    public ResponseEntity<Submission> getMySubmission(@PathVariable("marathonId") final String marathonId,
                                                      final Principal principal) {
        final Submission submission =
            this.submissionService.findByUserAndMarathon(PrincipalHelper.getUserFromPrincipal(principal),
                marathonId);
        return ResponseEntity.ok(submission);
    }

    @DeleteMapping("/{id}")
    @RolesAllowed({"ROLE_USER"})
    @PreAuthorize(value = "!isBanned()")
    @ApiIgnore
    public ResponseEntity<?> delete(@PathVariable("id") final int id, final Principal principal) {
        try {
            this.submissionService.delete(id, PrincipalHelper.getUserFromPrincipal(principal));
            return ResponseEntity.ok().build();
        } catch (final NotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

}
