package app.oengus.web.v1;

import app.oengus.entity.dto.OpponentSubmissionDto;
import app.oengus.helper.PrincipalHelper;
import app.oengus.service.CategoryService;
import app.oengus.spring.model.Views;
import com.fasterxml.jackson.annotation.JsonView;
import io.swagger.v3.oas.annotations.tags.Tag;
import javassist.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;

import java.security.Principal;

@RestController
@RequestMapping("/v1/marathons/{marathonId}/categories")
@Tag(name = "categories-v1")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @GetMapping("/{code}")
    @JsonView(Views.Public.class)
    @Operation(summary = "Find a multiplayer category by its code and return basic information"/*,
        response = OpponentSubmissionDto.class*/)
    public ResponseEntity<?> findCategoryByCode(@PathVariable("marathonId") final String marathonId,
                                                @PathVariable("code") final String code) {
        return ResponseEntity.ok(this.categoryService.findCategoryByCode(marathonId, code));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("canUpdateMarathon(#marathonId) && !isBanned() || isAdmin()")
    @Operation(hidden = true)
    public ResponseEntity<?> delete(@PathVariable("marathonId") final String marathonId,
                                    @PathVariable("id") final int id, final Principal principal) throws NotFoundException {
        this.categoryService.delete(id, PrincipalHelper.getUserFromPrincipal(principal));

        return ResponseEntity.ok().build();
    }

}
