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


@Slf4j
@CrossOrigin(value = "http://localhost:3000")
@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserDetailsService userDetailsService;

    @PostMapping("/set_password")
    public ResponseEntity<Void> setPassword(@RequestBody NewPasswordDTO newPassword) {

        return ResponseEntity.ok().build();
    }


    @GetMapping("/me")
    public ResponseEntity<UserDTO> getUser () {

        UserDTO user = new UserDTO();
        return ResponseEntity.ok(user);
    }

    @PatchMapping("/me")
    public ResponseEntity<UserDTO> updateUser (@RequestBody UpdateUserDTO updateUser ) {

        return ResponseEntity.ok(new UserDTO());
    }

    @PutMapping("/me/image")
    public ResponseEntity<?> updateAvatar(@ModelAttribute AvatarUpdateDTO avatarUpdateDTO) {
        MultipartFile file = avatarUpdateDTO.getFile();
        return ResponseEntity.ok("Аватар успешно обновлён !");
    }
}
