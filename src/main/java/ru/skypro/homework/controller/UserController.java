package ru.skypro.homework.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.dto.AvatarUpdateDTO;
import ru.skypro.homework.dto.NewPasswordDTO;
import ru.skypro.homework.dto.UpdateUserDTO;
import ru.skypro.homework.dto.UserDTO;
import ru.skypro.homework.service.UserService;

import javax.validation.Valid;


@Slf4j
@CrossOrigin(value = "http://localhost:3000")
@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

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
        UserDTO currentUser  = userService.getCurrentUser (); // Получаем текущего пользователя
        log.info("Обновление информации о пользователе: {}", currentUser .getEmail());
        UserDTO updatedUser  = userService.updateUser (updateUser );
        return ResponseEntity.ok(updatedUser );
    }




    @PutMapping("/me/image")
    public ResponseEntity<String> updateAvatar(@ModelAttribute AvatarUpdateDTO avatarUpdateDTO) {
        MultipartFile file = avatarUpdateDTO.getFile();
        log.info("Обновление аватара пользователя с файлом: {}", file.getOriginalFilename());
        userService.updateAvatar(file);
        return ResponseEntity.ok("Аватар успешно обновлён!");
    }
}


