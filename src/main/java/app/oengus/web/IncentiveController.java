package app.oengus.web;

import app.oengus.entity.model.Incentive;
import app.oengus.service.IncentiveService;
import app.oengus.spring.model.Views;
import com.fasterxml.jackson.annotation.JsonView;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import javassist.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.annotation.security.RolesAllowed;
import java.util.List;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/marathon/{marathonId}/incentive")
@Api(value = "/marathon/{marathonId}/incentive")
public class IncentiveController {

	@Autowired
	private IncentiveService incentiveService;

	@GetMapping
	@JsonView(Views.Public.class)
	@ApiOperation(value = "Get all incentives for a marathon",
			response = Incentive.class,
			responseContainer = "List")
	public ResponseEntity findAllForMarathon(@PathVariable("marathonId") final String marathonId,
	                                         @RequestParam(required = false, defaultValue = "true") final Boolean withLocked,
	                                         @RequestParam(required = false, defaultValue = "false") final Boolean withUnapproved) {
		try {
			return ResponseEntity.ok()
			                     .cacheControl(CacheControl.maxAge(1, TimeUnit.MINUTES))
			                     .body(this.incentiveService.findByMarathon(marathonId, withLocked, withUnapproved));
		} catch (final NotFoundException e) {
			return ResponseEntity.notFound().build();
		}
	}

	@PostMapping
	@JsonView(Views.Public.class)
	@RolesAllowed({"ROLE_USER"})
	@PreAuthorize("canUpdateMarathon(#marathonId) && !isBanned()")
	@ApiIgnore
	public ResponseEntity save(@PathVariable("marathonId") final String marathonId,
	                           @RequestBody final List<Incentive> incentives) {
		return ResponseEntity.ok(this.incentiveService.saveAll(incentives, marathonId));
	}

}
