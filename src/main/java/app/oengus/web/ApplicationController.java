package app.oengus.web;

import app.oengus.entity.dto.ApplicationUserInformationDto;
import app.oengus.entity.model.ApplicationUserInformation;
import app.oengus.entity.model.User;
import app.oengus.service.repository.ApplicationRepositoryService;
import app.oengus.spring.model.Views;
import com.fasterxml.jackson.annotation.JsonView;
import javassist.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.annotation.security.RolesAllowed;
import javax.validation.Valid;
import java.security.Principal;

import static app.oengus.helper.PrincipalHelper.getUserFromPrincipal;

@ApiIgnore
@RestController
@RequestMapping("/applications")
// TODO: remove and delegate to own controllers
public class ApplicationController {
    private final ApplicationRepositoryService applicationRepositoryService;

    @Autowired
    public ApplicationController(final ApplicationRepositoryService applicationRepositoryService) {
        this.applicationRepositoryService = applicationRepositoryService;
    }

    @GetMapping("/me")
    @RolesAllowed({"ROLE_USER"})
    @JsonView(Views.Public.class)
    public ResponseEntity<?> getOwnApplicationInfo(final Principal principal) throws NotFoundException {
        final User user = getUserFromPrincipal(principal);
        final ApplicationUserInformation infoForUser = this.applicationRepositoryService.getInfoForUser(user);

        return ResponseEntity.ok(infoForUser);
    }

    @PostMapping("/me")
    @RolesAllowed({"ROLE_USER"})
    @PreAuthorize("!isBanned()")
    @JsonView(Views.Public.class)
    public ResponseEntity<?> updateOwnApplicationInfo(
        final Principal principal,
        @RequestBody @Valid final ApplicationUserInformationDto infoPatch,
        final BindingResult bindingResult
    ) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(bindingResult.getAllErrors());
        }

        this.applicationRepositoryService.update(
            getUserFromPrincipal(principal),
            infoPatch
        );

        return ResponseEntity.noContent().build();
    }

}
