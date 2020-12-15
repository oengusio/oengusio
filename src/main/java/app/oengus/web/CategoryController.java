package app.oengus.web;

import app.oengus.entity.dto.OpponentSubmissionDto;
import app.oengus.service.CategoryService;
import app.oengus.spring.model.Views;
import com.fasterxml.jackson.annotation.JsonView;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

@RestController
@RequestMapping("/marathon/{marathonId}/category")
@Api(value = "/marathon/{marathonId}/category")
public class CategoryController {

	@Autowired
	private CategoryService categoryService;

	@GetMapping("/{code}")
	@JsonView(Views.Public.class)
	@ApiOperation(value = "Find a multiplayer category by its code and return basic information",
			response = OpponentSubmissionDto.class)
	public ResponseEntity findCategoryByCode(@PathVariable("marathonId") final String marathonId,
	                                         @PathVariable("code") final String code) {
		return ResponseEntity.ok(this.categoryService.findCategoryByCode(marathonId, code));
	}

	@DeleteMapping("/{id}")
	@PreAuthorize("canUpdateMarathon(#marathonId) && !isBanned() || isAdmin()")
	@ApiIgnore
	public ResponseEntity delete(@PathVariable("marathonId") final String marathonId,
	                             @PathVariable("id") final Integer id) {
		this.categoryService.delete(id);
		return ResponseEntity.ok().build();
	}

}
