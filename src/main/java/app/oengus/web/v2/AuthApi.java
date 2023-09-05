package app.oengus.web.v2;

import app.oengus.entity.dto.v2.auth.LoginDto;
import app.oengus.entity.dto.v2.auth.LoginResponseDto;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Tag(name = "auth")
@CrossOrigin(maxAge = 3600)
@RequestMapping("/v2/auth")
public interface AuthApi {

    @PostMapping("/login")
    @PreAuthorize("isAnonymous()")
    ResponseEntity<LoginResponseDto> login(@RequestBody LoginDto body);
}
