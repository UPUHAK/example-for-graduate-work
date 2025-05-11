package ru.skypro.homework.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.dto.AvatarUpdate;
import ru.skypro.homework.dto.NewPassword;
import ru.skypro.homework.dto.UpdateUser;
import ru.skypro.homework.dto.User;

import javax.validation.Valid;


@Slf4j
@CrossOrigin(value = "http://localhost:3000")
@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserDetailsService userDetailsService;

    @PostMapping("/set_password")
    public ResponseEntity<Void> setPassword(@RequestBody NewPassword newPassword) {

        return ResponseEntity.ok().build();
    }


    @GetMapping("/me")
    public ResponseEntity<User> getUser () {

        User user = new User();
        return ResponseEntity.ok(user);
    }

    @PatchMapping("/me")
    public ResponseEntity<User> updateUser (@RequestBody UpdateUser  updateUser ) {

        return ResponseEntity.ok(new User());
    }

    @PutMapping("/me/image")
    public ResponseEntity<?> updateAvatar(@ModelAttribute AvatarUpdate avatarUpdate) {
        MultipartFile file = avatarUpdate.getFile();
        return ResponseEntity.ok("Аватар успешно обновлён !");
    }
}
