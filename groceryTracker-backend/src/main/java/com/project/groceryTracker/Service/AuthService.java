
package com.project.groceryTracker.Service;

import com.project.groceryTracker.DTO.AuthResponseDTO;
import com.project.groceryTracker.DTO.LoginRequestDTO;
import com.project.groceryTracker.DTO.RegisterRequestDTO;
import com.project.groceryTracker.Entity.User;
import com.project.groceryTracker.Exception.DuplicateEmailException;
import com.project.groceryTracker.Repository.UserRepo;
import com.project.groceryTracker.Security.JwtService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

@Service
public class AuthService {

    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    public AuthService(
            UserRepo userRepo,
            PasswordEncoder passwordEncoder,
            AuthenticationManager authenticationManager,
            JwtService jwtService) {
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    @Transactional
    public AuthResponseDTO register(RegisterRequestDTO request) {

        String email = request.getEmail()
                .trim()
                .toLowerCase(java.util.Locale.ROOT);

        if (userRepo.existsByEmailIgnoreCase(email)) {
            throw new DuplicateEmailException(
                    "An account with this email already exists");
        }

        User user = new User();

        user.setFirstName(request.getFirstName().trim());
        user.setLastName(request.getLastName().trim());
        user.setEmail(email);
        user.setPhoneNum(request.getPhoneNum());
        user.setPassword(
                passwordEncoder.encode(request.getPassword()));

        User savedUser = userRepo.save(user);
        String token = jwtService.generateToken(user.getEmail());
        return new AuthResponseDTO(
                savedUser.getId(),
                savedUser.getFirstName(),
                savedUser.getLastName(),
                savedUser.getEmail(),
                token
                );
    }

    public AuthResponseDTO login(LoginRequestDTO request) {
        String email = request.getEmail().trim().toLowerCase(Locale.ROOT);

        Authentication authentication =
                authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(
                                email,
                                request.getPassword()
                        )
                );

        User user = userRepo
                .findByEmailIgnoreCase(authentication.getName())
                .orElseThrow();

        String token = jwtService.generateToken(user.getEmail());

        return new AuthResponseDTO(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                token
        );
    }
}