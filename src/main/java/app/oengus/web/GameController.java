package app.oengus.web;

import app.oengus.entity.dto.GameDto;
import app.oengus.service.ExportService;
import app.oengus.service.GameService;
import app.oengus.spring.model.Views;
import com.fasterxml.jackson.annotation.JsonView;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/marathon/{marathonId}/game")
@Api(value = "/marathon/{marathonId}/game")
public class GameController {

	@Autowired
	private GameService gameService;

	@Autowired
	private ExportService exportService;

	@GetMapping
	@JsonView(Views.Public.class)
	@ApiOperation(value = "Find all submitted games by marathon",
			response = GameDto.class,
			responseContainer = "List")
	public ResponseEntity findAllForMarathon(@PathVariable("marathonId") final String marathonId) {
		return ResponseEntity.ok()
		                     .cacheControl(CacheControl.maxAge(1, TimeUnit.MINUTES))
		                     .body(this.gameService.findByMarathon(marathonId));
	}

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

	@DeleteMapping("/{id}")
	@PreAuthorize("canUpdateMarathon(#marathonId) && !isBanned() || isAdmin()")
	@ApiIgnore
	public ResponseEntity delete(@PathVariable("marathonId") final String marathonId,
	                             @PathVariable("id") final Integer id) {
		this.gameService.delete(id);
		return ResponseEntity.ok().build();
	}

}
