package ru.skypro.homework.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
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
    /*
     Для хеширования паролей
     */
    private final PasswordEncoder passwordEncoder;
    Authentication authentication;

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

            if (passwordEncoder.matches(login.getPassword(), user.getPassword())) {
                userRepository.save(user);

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

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterDTO register) {
        log.info("Attempting to register user: {}", register.getUsername());

        /*
         Проверка на существование пользователя
         */
        if (userRepository.findByUsername(register.getUsername()).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Пользователь с таким именем уже существует.");
        }

        /*
         Проверка на наличие имени пользователя и пароля
         */
        if (register.getUsername() == null || register.getPassword() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Имя пользователя и пароль не могут быть пустыми.");
        }

        /*
         Создание нового пользователя
         */
        User newUser = new User();
        newUser.setUsername(register.getUsername());
        newUser.setPassword(passwordEncoder.encode(register.getPassword()));
        newUser.setFirstName(register.getFirstName());
        newUser.setLastName(register.getLastName());
        newUser.setPhone(register.getPhone());
        newUser.setEmail(register.getEmail());

        // Установка роли
        if (register.getRole() != null) {
            newUser.setRole(register.getRole());
        } else {
            newUser.setRole(Role.USER); // Установка роли по умолчанию
        }

        userRepository.save(newUser);
        log.info("User  registered successfully: {}", newUser.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED).body("Пользователь зарегистрирован успешно.");
    }

}

