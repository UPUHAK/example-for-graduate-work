package ru.skypro.homework.controller;

import io.swagger.v3.oas.annotations.Operation; // Импортируйте аннотацию
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.skypro.homework.dto.LoginDTO;
import ru.skypro.homework.model.User;
import ru.skypro.homework.repository.UserRepository;

import java.util.Optional;

@Slf4j
@CrossOrigin(value = "http://localhost:3000")
@RestController
public class LoginController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public LoginController(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Operation(summary = "Login a user", description = "Authenticate a user with email and password.")
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginDTO login) {
        log.info("Attempting to log in user: {}", login.getUsername());

        Optional<User> userOpt = userRepository.findByEmail(login.getUsername());

        if (userOpt.isPresent()) {
            User user = userOpt.get();

            if (passwordEncoder.matches(login.getPassword(), user.getPassword())) {
                Authentication authentication = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authentication);
                return ResponseEntity.ok().build();
            } else {
                // Неверный пароль
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Неверный пароль");
            }
        } else {
            // Пользователь не найден
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Пользователь не найден");
        }
    }
}

