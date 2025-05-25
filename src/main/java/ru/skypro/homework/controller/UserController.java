package ru.skypro.homework.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.dto.NewPasswordDTO;
import ru.skypro.homework.dto.UpdateUserDTO;
import ru.skypro.homework.dto.UserDTO;
import ru.skypro.homework.exception.UserNotFoundException;
import ru.skypro.homework.model.User;
import ru.skypro.homework.service.CurrentUserService;
import ru.skypro.homework.service.UserService;

import javax.validation.Valid;


@Slf4j
@CrossOrigin(value = "http://localhost:3000")
@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final CurrentUserService currentUserService;

    @PostMapping("/set_password")
    public ResponseEntity<Void> setPassword(@RequestBody @Valid NewPasswordDTO newPassword) {
        log.info("Установка нового пароля для текущего пользователя");
        userService.setPassword(newPassword);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/me")
    public ResponseEntity<UserDTO> getUser () {
        log.info("Получение информации о текущем пользователе");
        UserDTO user = userService.getCurrentUser ();
        return ResponseEntity.ok(user);
    }

    @PatchMapping("/me")
    public ResponseEntity<UserDTO> updateUser (@RequestBody @Valid UpdateUserDTO updateUser ) {
        UserDTO currentUser  = userService.getCurrentUser ();
        log.info("Обновление информации о пользователе: {}", currentUser .getEmail());
        UserDTO updatedUser  = userService.updateUser (updateUser );
        return ResponseEntity.ok(updatedUser );
    }
    @PutMapping(value = "/me/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> updateAvatar(
            @RequestParam("file") MultipartFile file) {

        if (file == null || file.isEmpty()) {
            log.warn("Попытка обновления аватара с пустым файлом.");
            return ResponseEntity.badRequest().body("Ошибка: файл пустой!");
        }

        User currentUser  = currentUserService.getCurrentUser ();


        if (currentUser  == null) {
            log.error("Текущий пользователь не найден.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Ошибка: пользователь не авторизован.");
        }

        log.info("Обновление аватара пользователя с файлом: {}", file.getOriginalFilename());

        try {
            userService.updateImage(currentUser .getId().intValue(), file); // Передаем userId и файл
            return ResponseEntity.ok("Аватар успешно обновлён!");
        } catch (UserNotFoundException e) {
            log.error("Ошибка обновления аватара: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Пользователь не найден.");
        } catch (IllegalArgumentException e) {
            log.error("Ошибка обновления аватара: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            log.error("Неизвестная ошибка при обновлении аватара: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Ошибка при обновлении аватара. Попробуйте снова.");
        }
    }



}


