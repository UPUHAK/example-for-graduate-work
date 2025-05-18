package ru.skypro.homework.controller;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.skypro.homework.dto.LoginDTO;
import ru.skypro.homework.dto.RegisterDTO;

@Slf4j
@CrossOrigin(value = "http://localhost:3000")
@RestController
public class AuthController {



    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginDTO login) {
        // Заглушка для логики аутентификации
        log.info("Attempting to log in user: {}", login.getUsername());

        // Пример успешной аутентификации для пользователя "user" с паролем "password"
        if ("user".equals(login.getUsername()) && "password".equals(login.getPassword())) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterDTO register) {
        // Заглушка для логики регистрации
        log.info("Attempting to register user: {}", register.getUsername());

        // Пример успешной регистрации
        if (register.getUsername() != null && register.getPassword() != null) {
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
}

