package ru.skypro.homework.controller;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.skypro.homework.dto.LoginDTO;
import ru.skypro.homework.dto.RegisterDTO;
import ru.skypro.homework.dto.Role;
import ru.skypro.homework.model.User;
import ru.skypro.homework.repository.UserRepository;

import java.util.Optional;

@Slf4j
@CrossOrigin(value = "http://localhost:3000")
@RestController
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder; // Для хеширования паролей

    public AuthController(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginDTO login) {
        log.info("Attempting to log in user: {}", login.getUsername());

        Optional<User> userOpt = userRepository.findByUsername(login.getUsername());

        if (userOpt.isPresent()) {
            User user = userOpt.get();
            // Проверяем пароль
            if (passwordEncoder.matches(login.getPassword(), user.getPassword())) {
                // Успешная аутентификация
                return ResponseEntity.ok().build();
            } else {
                // Неверный пароль
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
        } else {
            // Пользователь не найден — создаём нового
            User newUser = new User();
            newUser.setUsername(login.getUsername());
            newUser.setPassword(passwordEncoder.encode(login.getPassword()));
            // Можно задать другие поля по умолчанию, например роль
            newUser.setRole(Role.USER);

            userRepository.save(newUser);

            return ResponseEntity.status(HttpStatus.CREATED).build();
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterDTO register) {
        log.info("Attempting to register user: {}", register.getUsername());

        // Проверка на существование пользователя
        if (userRepository.findByUsername(register.getUsername()).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Пользователь с таким именем уже существует.");
        }

        // Проверка на наличие имени пользователя и пароля
        if (register.getUsername() == null || register.getPassword() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Имя пользователя и пароль не могут быть пустыми.");
        }

        // Создание нового пользователя
        User newUser  = new User();
        newUser .setUsername(register.getUsername());
        newUser .setPassword(passwordEncoder.encode(register.getPassword()));
        newUser .setFirstName(register.getFirstName());
        newUser .setLastName(register.getLastName());
        newUser .setPhone(register.getPhone());
        newUser .setEmail(register.getEmail());
        newUser .setRole(Role.USER); // например, установка роли по умолчанию

        // Сохранение пользователя в базе данных
        userRepository.save(newUser );

        log.info("User  registered successfully: {}", newUser .getUsername());
        return ResponseEntity.status(HttpStatus.CREATED).body("Пользователь зарегистрирован успешно.");
    }

}

