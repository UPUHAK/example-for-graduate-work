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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.dto.ImageDTO;
import ru.skypro.homework.dto.NewPasswordDTO;
import ru.skypro.homework.dto.UpdateUserDTO;
import ru.skypro.homework.dto.UpdateUserDTO;
import ru.skypro.homework.dto.UserDTO;
import ru.skypro.homework.model.User;
import ru.skypro.homework.service.CurrentUserService;
import ru.skypro.homework.service.CurrentUserService;
import ru.skypro.homework.service.ImageService;
import ru.skypro.homework.service.UserService;

import javax.persistence.EntityNotFoundException;
import javax.validation.Valid;

@Slf4j
@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final CurrentUserService currentUserService;
    private final ImageService imageService;

    @PostMapping("/set_password")
    @Operation(
            summary = "Установка нового пароля для текущего пользователя",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Пароль успешно установлен", content = @Content()),
                    @ApiResponse(responseCode = "400", description = "Некорректный запрос", content = @Content()),
                    @ApiResponse(responseCode = "404", description = "Пользователь не найден", content = @Content())
            }
    )
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
                            schema = @Schema(implementation = UserDTO.class))
                    ),
                    @ApiResponse(responseCode = "401", description = "Неавторизованный доступ", content = @Content()),
                    @ApiResponse(responseCode = "404", description = "Пользователь не найден", content = @Content())
            }
    )
    public ResponseEntity<UserDTO> getCurrentUser() {
        UserDTO currentUser = userService.getCurrentUser();
        return ResponseEntity.ok(currentUser);
    }

    @PatchMapping("/me")
    @Operation(
            summary = "Обновление информации о текущем пользователе",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Информация о пользователе успешно обновлена", content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = UserDTO.class))
                    ),
                    @ApiResponse(responseCode = "400", description = "Некорректный запрос", content = @Content()),
                    @ApiResponse(responseCode = "404", description = "Пользователь не найден", content = @Content())
            }
    )
    public ResponseEntity<UserDTO> updateUser(@RequestBody @Valid UpdateUserDTO updateUser) {
        log.info("Обновление информации о пользователе");
        UserDTO updatedUser = userService.updateUser(updateUser);
        return ResponseEntity.ok(updatedUser);
    }

    @PutMapping(value = "/me/image")
    @Operation(
            summary = "Обновление аватара текущего пользователя",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Аватар успешно обновлён", content = @Content()),
                    @ApiResponse(responseCode = "400", description = "Ошибка: файл пустой или некорректный", content = @Content()),
                    @ApiResponse(responseCode = "401", description = "Пользователь не авторизован", content = @Content()),
                    @ApiResponse(responseCode = "404", description = "Пользователь не найден", content = @Content()),
                    @ApiResponse(responseCode = "500", description = "Ошибка при обновлении аватара", content = @Content())
            }
    )
    public ResponseEntity<String> updateImage(@RequestParam("file") MultipartFile file) {
        if (file == null || file.isEmpty()) {
            log.warn("Попытка обновления аватара с пустым файлом.");
            return ResponseEntity.badRequest().body("Ошибка: файл пустой!");
        }

        try {
            Integer currentUserId = getCurrentUserId();
            userService.updateImageForUser(currentUserId, file);
            log.info("Аватар успешно обновлён для пользователя с ID {}", currentUserId);
            return ResponseEntity.ok("Аватар успешно обновлён!");
        } catch (EntityNotFoundException e) {
            log.error("Пользователь не найден: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Ошибка: пользователь не найден.");
        } catch (IllegalArgumentException e) {
            log.warn("Ошибка при загрузке файла: {}", e.getMessage());
            return ResponseEntity.badRequest().body("Ошибка: " + e.getMessage());
        } catch (Exception e) {
            log.error("Неизвестная ошибка при обновлении аватара: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Ошибка при обновлении аватара. Попробуйте снова.");
        }
    }

    // Вспомогательный метод для получения ID текущего пользователя
    private Integer getCurrentUserId() {
        User currentUser = currentUserService.getAuthenticatedUser();
        return currentUser.getId();
    }
}








