package backend.authentication.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;

import backend.authentication.dto.AccountResponse;
import backend.authentication.dto.AuthenticationResponse;
import backend.authentication.dto.GoogleTokenResponse;
import backend.authentication.dto.LoginRequest;
import backend.authentication.dto.RegisterRequest;
import backend.authentication.service.AuthenticationService;
import backend.authentication.service.GoogleAuthService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthenticationController {
    private final AuthenticationService authenticationService;
    private final GoogleAuthService googleAuthService;

    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(@RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authenticationService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(authenticationService.login(request));
    }

    @PostMapping("/google")
    public ResponseEntity<AuthenticationResponse> loginWithGoogle(@RequestBody Map<String, String> body) {
        String code = body.get("code");

        GoogleTokenResponse tokenResponse = googleAuthService.exchangeCodeForToken(code);
        GoogleIdToken.Payload payload = googleAuthService.parseIdToken(tokenResponse.getId_token());

        AuthenticationResponse authResponse = authenticationService.loginOrRegisterGoogleUser(payload.getEmail(),
                (String) payload.get("name"));
        return ResponseEntity.ok(authResponse);
    }

    @GetMapping("/verify")
    public ResponseEntity<Map<String, String>> verify(@RequestParam String token) {
        return ResponseEntity.ok(Map.of("message", authenticationService.verify(token)));
    }

    @GetMapping("/account")
    public ResponseEntity<AccountResponse> getAccount() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        AccountResponse accountResponse = authenticationService.getUserInfo(username);
        return ResponseEntity.ok(accountResponse);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        return ResponseEntity.ok("Logged out successfully");
    }
}