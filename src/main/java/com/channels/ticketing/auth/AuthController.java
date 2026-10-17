package com.channels.ticketing.auth;

import com.channels.ticketing.audit.AuditService;
import com.channels.ticketing.user.UserRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.*;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthenticationManager manager;
    private final JwtEncoder encoder;
    private final UserRepository users;
    private final AuditService audit;

    public AuthController(AuthenticationManager manager, JwtEncoder encoder, UserRepository users, AuditService audit) {
        this.manager = manager;
        this.encoder = encoder;
        this.users = users;
        this.audit = audit;
    }

    public record LoginRequest(@NotBlank String username, @NotBlank String password) {
    }

    public record LoginResponse(String accessToken, String tokenType, int expiresIn) {
    }

    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest request) {
        try {
            manager.authenticate(new UsernamePasswordAuthenticationToken(request.username(), request.password()));
        } catch (AuthenticationException ex) {
            audit.login(request.username(), false);
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
        }
        var user = users.findByUsername(request.username()).orElseThrow();
        var now = Instant.now();
        var claims = JwtClaimsSet.builder().issuer("channels-ticketing").subject(user.username).issuedAt(now).expiresAt(now.plusSeconds(900)).claim("role", user.role.name()).build();
        var token = encoder.encode(JwtEncoderParameters.from(JwsHeader.with(MacAlgorithm.HS256).build(), claims)).getTokenValue();
        audit.login(user.username, true);
        return new LoginResponse(token, "Bearer", 900);
    }
}
