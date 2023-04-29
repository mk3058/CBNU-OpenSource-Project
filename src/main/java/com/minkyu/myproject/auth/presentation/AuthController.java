package com.minkyu.myproject.auth.presentation;

import com.minkyu.myproject.auth.application.AuthService;
import com.minkyu.myproject.auth.domain.AccessToken;
import com.minkyu.myproject.auth.domain.RefreshToken;
import com.minkyu.myproject.auth.domain.Token;
import com.minkyu.myproject.auth.presentation.dto.AuthTokens;
import com.minkyu.myproject.auth.presentation.dto.JoinRequest;
import com.minkyu.myproject.auth.presentation.dto.LoginRequest;
import com.minkyu.myproject.security.jwt.JwtAuthToken;
import org.apache.coyote.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
public class AuthController {

    private final AuthService authService;

    private final AuthenticationManager authenticationManager;

    public AuthController(AuthService authService, AuthenticationManager authenticationManager) {
        this.authService = authService;
        this.authenticationManager = authenticationManager;
    }

    @PostMapping("/join")
    public ResponseEntity<Void> join(@RequestBody JoinRequest dto) {
        URI uri = authService.join(dto);
        return ResponseEntity.created(uri).build();
    }

    @PostMapping("/login")
    public ResponseEntity<AuthTokens> login(@RequestBody LoginRequest dto) {
        JwtAuthToken jwtAuthToken = new JwtAuthToken(dto);
        Authentication authentication = authenticationManager.authenticate(jwtAuthToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        AuthTokens authTokens = (AuthTokens) authentication.getDetails();
        return ResponseEntity.ok(authTokens);
    }

    @GetMapping("/{refreshToken}/reissue")
    public ResponseEntity<AuthTokens> reissue(@PathVariable("refreshToken") String token) {
        Token<RefreshToken> refreshToken = Token.of(RefreshToken.class, token);
        Token<AccessToken> accessToken = authService.reissue(refreshToken);

        AuthTokens authTokens = new AuthTokens(accessToken, refreshToken);
        return ResponseEntity.ok(authTokens);
    }
}
