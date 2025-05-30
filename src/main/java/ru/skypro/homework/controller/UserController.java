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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.dto.ImageDTO;
import ru.skypro.homework.dto.NewPasswordDTO;
import ru.skypro.homework.dto.UpdateUserDTO;
import ru.skypro.homework.dto.UserDTO;
import ru.skypro.homework.model.User;
import ru.skypro.homework.service.CurrentUserService;
import ru.skypro.homework.service.ImageService;
import ru.skypro.homework.service.UserService;
import java.io.IOException;
import javax.persistence.EntityNotFoundException;
import javax.validation.Valid;



@Slf4j
@CrossOrigin(value = "http://localhost:3000")
@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final CurrentUserService currentUserService;
    private final ImageService imageService;

    @PostMapping("/set_password")
    public ResponseEntity<Void> setPassword(@RequestBody @Valid NewPasswordDTO newPassword) {
        log.info("Установка нового пароля для текущего пользователя");
        userService.setPassword(newPassword);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/me")
    @Operation(
            summary = "Получение информации об авторизованном пользователе",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Информация о пользователе", content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = User.class))
                    ),
                    @ApiResponse(responseCode = "401", description = "Неавторизованный доступ", content = @Content()),
                    @ApiResponse(responseCode = "404", description = "Пользователь не найден", content = @Content()
                    )
            }
    )

    @PatchMapping("/me")
    public ResponseEntity<UserDTO> updateUser (@RequestBody @Valid UpdateUserDTO updateUser ) {
        UserDTO currentUser  = userService.getCurrentUser ();
        log.info("Обновление информации о пользователе: {}", currentUser .getEmail());
        UserDTO updatedUser  = userService.updateUser (updateUser );
        return ResponseEntity.ok(updatedUser );
    }
    @PutMapping(value = "/me/image")
    public ResponseEntity<String> updateImage(@RequestParam("file") MultipartFile file) {
        // Проверка на пустой файл
        if (file == null || file.isEmpty()) {
            log.warn("Попытка обновления аватара с пустым файлом.");
            return ResponseEntity.badRequest().body("Ошибка: файл пустой!");
        }

        // Получение текущего пользователя
        User currentUser  = currentUserService.getAuthenticatedUser ();

        if (currentUser  == null) {
            log.error("Текущий пользователь не найден.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Ошибка: пользователь не авторизован.");
        }

        // Обновление изображения через сервис
        try {
            String filePath = imageService.saveFile(file); // Убедитесь, что этот метод может выбросить IOException
            log.info("Обновление аватара пользователя с файлом по пути: {}", filePath);

            // Создайте DTO для изображения
            ImageDTO imageDTO = new ImageDTO();
            imageDTO.setUserId(currentUser .getId()); // Установите ID текущего пользователя

            // Вызовите метод для сохранения изображения на файловой системе
            userService.saveImageFromFilePath(filePath, imageDTO);

            return ResponseEntity.ok("Аватар успешно обновлён!");
        } catch (IllegalArgumentException e) {
            log.warn("Ошибка при загрузке файла: {}", e.getMessage());
            return ResponseEntity.badRequest().body("Ошибка: " + e.getMessage());
        } catch (EntityNotFoundException e) {
            log.error("Пользователь с ID {} не найден.", currentUser .getId());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Ошибка: пользователь не найден.");
        } catch (Exception e) {
            log.error("Неизвестная ошибка при обновлении аватара: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Ошибка при обновлении аватара. Попробуйте снова.");
        }
    }





}






