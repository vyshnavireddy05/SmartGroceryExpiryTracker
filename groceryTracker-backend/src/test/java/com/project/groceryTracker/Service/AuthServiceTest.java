package com.project.groceryTracker.Service;

import com.project.groceryTracker.DTO.LoginRequestDTO;
import com.project.groceryTracker.DTO.RegisterRequestDTO;
import com.project.groceryTracker.Entity.User;
import com.project.groceryTracker.Exception.DuplicateEmailException;
import com.project.groceryTracker.Repository.UserRepo;
import com.project.groceryTracker.Security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Base64;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AuthServiceTest {

    private UserRepo userRepo;
    private AuthenticationManager authenticationManager;
    private AuthService authService;

    @BeforeEach
    void setUp() {
        userRepo = mock(UserRepo.class);
        authenticationManager = mock(AuthenticationManager.class);
        authService = new AuthService(
                userRepo,
                new BCryptPasswordEncoder(),
                authenticationManager,
                new JwtService(
                        Base64.getEncoder().encodeToString(new byte[32]),
                        60_000
                )
        );
    }

    @Test
    void registrationNormalizesEmailAndStoresOnlyEncodedPassword() {
        RegisterRequestDTO request = new RegisterRequestDTO();
        request.setFirstName("Ada");
        request.setLastName("Lovelace");
        request.setEmail("  ADA@example.com ");
        request.setPhoneNum("+15551234567");
        request.setPassword("correct-horse");
        when(userRepo.existsByEmailIgnoreCase("ada@example.com"))
                .thenReturn(false);
        when(userRepo.save(any(User.class))).thenAnswer(invocation -> {
            User saved = invocation.getArgument(0);
            saved.setId(5L);
            return saved;
        });

        var response = authService.register(request);

        assertEquals(5L, response.getId());
        assertEquals("ada@example.com", response.getEmail());
        assertTrue(new BCryptPasswordEncoder().matches(
                "correct-horse",
                userRepoSavedPassword()
        ));
        assertNotNull(response.getToken());
    }

    @Test
    void registrationRejectsDuplicateEmail() {
        RegisterRequestDTO request = new RegisterRequestDTO();
        request.setEmail("user@example.com");
        when(userRepo.existsByEmailIgnoreCase("user@example.com"))
                .thenReturn(true);

        assertThrows(
                DuplicateEmailException.class,
                () -> authService.register(request)
        );
        verify(userRepo, never()).save(any(User.class));
    }

    @Test
    void loginPropagatesInvalidCredentials() {
        LoginRequestDTO request = new LoginRequestDTO();
        request.setEmail("user@example.com");
        request.setPassword("wrong-password");
        when(authenticationManager.authenticate(any(Authentication.class)))
                .thenThrow(new BadCredentialsException("invalid"));

        assertThrows(
                BadCredentialsException.class,
                () -> authService.login(request)
        );
    }

    private String userRepoSavedPassword() {
        var captor = org.mockito.ArgumentCaptor.forClass(User.class);
        verify(userRepo).save(captor.capture());
        return captor.getValue().getPassword();
    }
}
