
package com.project.groceryTracker.Controller;

import com.project.groceryTracker.DTO.AuthResponseDTO;
import com.project.groceryTracker.DTO.LoginRequestDTO;
import com.project.groceryTracker.DTO.RegisterRequestDTO;
import com.project.groceryTracker.Service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponseDTO> register(
            @jakarta.validation.Valid
            @RequestBody RegisterRequestDTO request) {

        AuthResponseDTO response = authService.register(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(
            @jakarta.validation.Valid
            @RequestBody LoginRequestDTO request) {

        AuthResponseDTO response = authService.login(request);

        return ResponseEntity.ok(response);
    }
}