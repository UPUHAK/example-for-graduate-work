package ru.skypro.homework.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import ru.skypro.homework.dto.ImageDTO;
import ru.skypro.homework.dto.NewPasswordDTO;
import ru.skypro.homework.dto.UpdateUserDTO;
import ru.skypro.homework.dto.UserDTO;;
import ru.skypro.homework.mapper.UserMapper;
import ru.skypro.homework.model.User;
import ru.skypro.homework.repository.UserRepository;
import ru.skypro.homework.service.CurrentUserService;
import ru.skypro.homework.service.ImageService;
import ru.skypro.homework.service.UserService;

import javax.persistence.EntityNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final CurrentUserService currentUserService;
    private final ImageService imageService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    @Override
    public UserDTO getCurrentUser() {
        User user = currentUserService.getCurrentUser();
        return userMapper.toDTO(user);
    }

    @Override
    public UserDTO updateUser(UpdateUserDTO updateUserDTO) {
        User user = currentUserService.getCurrentUser();
        userMapper.updateEntityFromDTO(updateUserDTO, user);
        userRepository.save(user);
        return userMapper.toDTO(user);
    }

    @Override
    public void updateImage(MultipartFile file) {
        User user = currentUserService.getCurrentUser ();

        if (file.isEmpty()) {
            log.warn("User  {} tried to upload an empty file.", user.getId());
            throw new IllegalArgumentException("Ошибка: файл пустой!");
        }

        // Создание объекта ImageDTO
        ImageDTO imageDTO = new ImageDTO();
        imageDTO.setUserId(user.getId().intValue());
        imageDTO.setImageUrl(file.getOriginalFilename());
        try {
            imageDTO.setData(file.getBytes()); // Преобразуем файл в массив байтов
        } catch (IOException e) {
            log.error("Error reading file for user {}: {}", user.getId(), e.getMessage());
            throw new RuntimeException("Ошибка при чтении файла. Попробуйте снова.", e);
        }
        imageDTO.setAdId(null);

        // Изменяем вызов saveToDatabase, чтобы передать необходимые параметры
        try {
            Path imagePath = Paths.get("path/to/save/" + file.getOriginalFilename());
            imageService.saveToDatabase(imageDTO, imagePath, file);
            log.info("User  {} uploaded an image successfully.", user.getId());
        } catch (EntityNotFoundException e) {
            log.error("User  not found: {}", e.getMessage());
            throw new RuntimeException("Пользователь не найден.", e);
        }
    }



    @Override
    public List<UserDTO> getAllUsers() {
        List<User> users = userRepository.findAll();
        return userMapper.toDTOList(users);
    }

    @Transactional
    @Override
    public void setPassword(NewPasswordDTO newPassword) {
        User user = currentUserService.getCurrentUser();
        user.setPassword(passwordEncoder.encode(newPassword.getNewPassword()));
        userRepository.save(user);
    }

    @Override
    public User createUser(UserDTO userDto) {
        if (userRepository.existsByEmail(userDto.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }

        if (userRepository.existsByUsername(userDto.getUsername())) {
            throw new IllegalArgumentException("Username already exists");
        }

        User user = new User();
        user.setUsername(userDto.getUsername());
        user.setPassword(userDto.getPassword()); // Не забудьте установить пароль
        user.setFirstName(userDto.getFirstName());
        user.setLastName(userDto.getLastName());
        user.setPhone(userDto.getPhone());
        user.setRole(userDto.getRole()); // Здесь устанавливается роль
        user.setEmail(userDto.getEmail());

        log.info("Creating user with username: {}, email: {}, role: {}", user.getUsername(), user.getEmail(), user.getRole());
        return userRepository.save(user);
    }

}






// Метод для проверки допустимых типов изображений
// private boolean isValidImageType(String contentType) {
//    return contentType != null && (contentType.equals("image/jpeg") || contentType.equals("image/png"));
// }








