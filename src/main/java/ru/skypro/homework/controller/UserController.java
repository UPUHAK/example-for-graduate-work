package ru.skypro.homework.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import ru.skypro.homework.dto.NewPasswordDTO;
import ru.skypro.homework.dto.UpdateUserDTO;
import ru.skypro.homework.dto.UserDTO;
import ru.skypro.homework.exception.InvalidCurrentPasswordException;
import ru.skypro.homework.exception.InvalidPasswordException;
import ru.skypro.homework.exception.UserNotFoundException;
import ru.skypro.homework.service.UserService;
import javax.validation.Valid;

@Slf4j
@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @PostMapping("/set_password")
    public ResponseEntity<Void> setPassword(@RequestBody @Valid NewPasswordDTO newPassword) {
        log.info("Запрос на смену пароля");
        try {
            userService.setPassword(newPassword);
            return ResponseEntity.ok().build();
        } catch (UserNotFoundException e) {
            log.error("Ошибка: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (InvalidCurrentPasswordException e) {
            log.error("Ошибка: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (InvalidPasswordException e) {
            log.error("Ошибка: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("Ошибка при смене пароля: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserDTO> getCurrentUser () {
        log.info("Запрос на получение информации о текущем пользователе");
        UserDTO currentUser  = userService.getCurrentUser ();
        return ResponseEntity.ok(currentUser );
    }

    @PatchMapping("/me")
    public ResponseEntity<UserDTO> updateUser (@RequestBody @Valid UpdateUserDTO updateUser ) {
        log.info("Обновление информации о пользователе");
        UserDTO updatedUser  = userService.updateUser (updateUser );
        return ResponseEntity.ok(updatedUser );
    }

    @PatchMapping(value = "/me/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> updateUserImage(@RequestPart("file") MultipartFile file) {
        if (file == null || file.isEmpty()) {
            log.warn("Попытка обновления аватара с пустым файлом.");
            return ResponseEntity.badRequest().body("Ошибка: файл пустой!");
        }

        try {
            userService.updateUserImage(file);
            log.info("Аватар успешно обновлён");
            return ResponseEntity.ok("Аватар успешно обновлён!");
        } catch (Exception e) {
            log.error("Ошибка при обновлении аватара: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Ошибка при обновлении аватара. Попробуйте снова.");
        }
    }
}









