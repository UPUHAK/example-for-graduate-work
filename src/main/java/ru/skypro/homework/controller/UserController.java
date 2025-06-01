package ru.skypro.homework.controller;

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
import ru.skypro.homework.exception.FileStorageException;
import ru.skypro.homework.exception.InvalidCurrentPasswordException;
import ru.skypro.homework.exception.InvalidPasswordException;
import ru.skypro.homework.exception.UserNotFoundException;
import ru.skypro.homework.service.UserService;
import javax.validation.Valid;

import ru.skypro.homework.service.ImageStorageService;

import java.io.IOException;


@Slf4j
@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final ImageStorageService imageStorageService; // Внедряем ImageStorageService

    @PostMapping("/set_password")
    public ResponseEntity<String> setPassword(@RequestBody @Valid NewPasswordDTO newPassword) {
        log.info("Запрос на смену пароля");

        if (newPassword == null) {
            log.error("Ошибка: Новый пароль не может быть null");
            return ResponseEntity.badRequest().body("Новый пароль не может быть null");
        }

        try {
            userService.setPassword(newPassword);
            return ResponseEntity.ok().body("Пароль успешно изменён");
        } catch (UserNotFoundException e) {
            log.error("Ошибка: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Пользователь не найден");
        } catch (InvalidCurrentPasswordException e) {
            log.error("Ошибка: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Неверный текущий пароль");
        } catch (InvalidPasswordException e) {
            log.error("Ошибка: {}", e.getMessage());
            return ResponseEntity.badRequest().body("Новый пароль не соответствует требованиям");
        } catch (Exception e) {
            log.error("Ошибка при смене пароля: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Произошла ошибка при изменении пароля");
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
        // Проверка на пустой файл
        if (file == null || file.isEmpty()) {
            log.warn("Попытка обновления аватара с пустым файлом.");
            return ResponseEntity.badRequest().body("Ошибка: файл пустой!");
        }

        try {
            // Сохраняем файл и получаем путь
            String imagePath = imageStorageService.store(file);

            // Обновляем путь к изображению в UserService (предполагается, что вы это делаете)
            // userService.updateUser ImagePath(imagePath); // пример, как можно обновить путь

            log.info("Аватар успешно обновлён");

            // Возвращаем успешный ответ
            return ResponseEntity.ok("Аватар успешно обновлён: " + imagePath);
        } catch (IOException e) {
            log.error("Ошибка при обновлении аватара: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Ошибка при обновлении аватара. Попробуйте снова.");
        } catch (Exception e) {
            log.error("Неизвестная ошибка: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Неизвестная ошибка. Попробуйте снова.");
        }
    }


    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> uploadFile(@RequestPart("file") MultipartFile file) {
        if (file == null || file.isEmpty()) {
            log.warn("Попытка загрузки файла с пустым содержимым.");
            return ResponseEntity.badRequest().body("Ошибка: файл пустой!");
        }

        try {
            String filePath = imageStorageService.store(file); // Сохраняем файл и получаем путь
            log.info("Файл успешно загружен: {}", filePath);
            return ResponseEntity.ok("Файл успешно загружен: " + filePath);
        } catch (FileStorageException e) {
            log.error("Ошибка: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            log.error("Ошибка при загрузке файла: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Ошибка при загрузке файла. Попробуйте снова.");
        }
    }
}










