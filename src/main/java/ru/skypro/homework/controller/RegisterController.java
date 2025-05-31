package ru.skypro.homework.controller;

import io.swagger.v3.oas.annotations.Operation; // Импортируйте аннотацию
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.skypro.homework.dto.RegisterDTO;
import ru.skypro.homework.dto.Role;
import ru.skypro.homework.model.User;
import ru.skypro.homework.repository.UserRepository;

@Slf4j
@CrossOrigin(value = "http://localhost:3000")
@RestController
public class RegisterController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public RegisterController(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Operation(summary = "Register a new user", description = "Create a new user account with email and password.")
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterDTO register) {
        log.info("Attempting to register user: {}", register.getUsername());

        // Проверка на существование пользователя
        if (userRepository.findByEmail(register.getUsername()).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Пользователь с таким именем уже существует.");
        }

        // Проверка на наличие имени пользователя и пароля
        if (register.getUsername() == null || register.getPassword() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Имя пользователя и пароль не могут быть пустыми.");
        }

        // Создание нового пользователя
        User newUser  = new User();
        newUser .setEmail(register.getUsername());
        newUser .setPassword(passwordEncoder.encode(register.getPassword()));
        newUser .setFirstName(register.getFirstName());
        newUser .setLastName(register.getLastName());
        newUser .setPhone(register.getPhone());

        // Установка роли
        if (register.getRole() != null) {
            newUser .setRole(register.getRole());
        } else {
            newUser .setRole(Role.USER); // Установка роли по умолчанию
        }

        userRepository.save(newUser );
        log.info("User  registered successfully: {}", newUser .getEmail());
        return ResponseEntity.status(HttpStatus.CREATED).body("Пользователь зарегистрирован успешно.");
    }
}

