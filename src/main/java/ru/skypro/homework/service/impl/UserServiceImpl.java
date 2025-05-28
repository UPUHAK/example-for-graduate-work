package ru.skypro.homework.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import ru.skypro.homework.dto.ImageDTO;
import ru.skypro.homework.dto.NewPasswordDTO;
import ru.skypro.homework.dto.UpdateUserDTO;
import ru.skypro.homework.dto.UserDTO;;
import ru.skypro.homework.exception.UserNotFoundException;
import ru.skypro.homework.mapper.UserMapper;
import ru.skypro.homework.model.User;
import ru.skypro.homework.repository.UserRepository;
import ru.skypro.homework.service.CurrentUserService;
import ru.skypro.homework.service.ImageService;
import ru.skypro.homework.service.UserService;

import javax.persistence.EntityNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
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
    @PreAuthorize("isAuthenticated()")
    public UserDTO getCurrentUser () {
        User user = currentUserService.getAuthenticatedUser ();
        return userMapper.toDTO(user);
    }

    @Override
    @PreAuthorize("isAuthenticated()")
    public UserDTO updateUser (UpdateUserDTO updateUserDTO) {
        User user = currentUserService.getAuthenticatedUser ();
        userMapper.updateEntityFromDTO(updateUserDTO, user);
        userRepository.save(user);
        return userMapper.toDTO(user);
    }


    @Override
    @PreAuthorize("isAuthenticated()")
    public void updateImage(MultipartFile file) {
        User user = currentUserService.getAuthenticatedUser (); // Получаем авторизованного пользователя

        validateFile(file, user); // Проверяем файл
        ImageDTO imageDTO = createImageDTO(user, file); // Создаем ImageDTO
        saveImageToFileSystem(file, imageDTO); // Сохраняем изображение
    }

    private void validateFile(MultipartFile file, User user) {
        if (file.isEmpty()) {
            log.warn("Пользователь {} попытался загрузить пустой файл.", user.getId());
            throw new IllegalArgumentException("Ошибка: файл пустой!");
        }

        String contentType = file.getContentType();
        if (!"image/jpeg".equals(contentType) && !"image/png".equals(contentType)) {
            log.warn("Пользователь {} загрузил неподдерживаемый тип файла: {}", user.getId(), contentType);
            throw new IllegalArgumentException("Ошибка: поддерживаются только JPEG и PNG файлы.");
        }

        if (file.getSize() > 2 * 1024 * 1024) { // 2 МБ
            log.warn("Пользователь {} загрузил файл, превышающий допустимый лимит.", user.getId());
            throw new IllegalArgumentException("Ошибка: размер файла не должен превышать 2 МБ.");
        }
    }

    private ImageDTO createImageDTO(User user, MultipartFile file) {
        ImageDTO imageDTO = new ImageDTO();
        imageDTO.setUserId(user.getId().intValue());
        imageDTO.setImageUrl(file.getOriginalFilename());

        try {
            imageDTO.setData(file.getBytes());
        } catch (IOException e) {
            log.error("Ошибка чтения файла для пользователя {}: {}", user.getId(), e.getMessage());
            throw new RuntimeException("Ошибка при чтении файла. Попробуйте снова.", e);
        }

        return imageDTO;
    }

    private void saveImageToFileSystem(MultipartFile file, ImageDTO imageDTO) {
        try {
            String originalFilename = StringUtils.cleanPath(file.getOriginalFilename());
            Path imagePath = Paths.get("path/to/save/" + originalFilename);

            Files.createDirectories(imagePath.getParent());

            imageService.saveToDatabase(imageDTO, imagePath, file);
            log.info("Пользователь {} успешно обновил аватар: {}", imageDTO.getUserId(), originalFilename);
        } catch (EntityNotFoundException e) {
            log.error("Ошибка сохранения: пользователь не найден: {}", e.getMessage());
            throw new RuntimeException("Пользователь не найден.", e);
        } catch (IOException e) {
            log.error("Ошибка при сохранении файла для пользователя {}: {}", imageDTO.getUserId(), e.getMessage());
            throw new RuntimeException("Ошибка при сохранении файла. Попробуйте снова.", e);
        }
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public List<UserDTO> getAllUsers() {
        List<User> users = userRepository.findAll();
        return userMapper.toDTOList(users);
    }

    @Transactional
    @Override
    @PreAuthorize("isAuthenticated()")
    public void setPassword(NewPasswordDTO newPassword) {
        User user = currentUserService.getAuthenticatedUser ();
        user.setPassword(passwordEncoder.encode(newPassword.getNewPassword()));
        userRepository.save(user);
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public User createUser (UserDTO userDto) {
        if (userRepository.existsByEmail(userDto.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }

        if (userRepository.existsByUsername(userDto.getUsername())) {
            throw new IllegalArgumentException("Username already exists");
        }

        User user = new User();
        user.setUsername(userDto.getUsername());
        user.setPassword(userDto.getPassword());
        user.setFirstName(userDto.getFirstName());
        user.setLastName(userDto.getLastName());
        user.setPhone(userDto.getPhone());
        user.setRole(userDto.getRole());
        user.setEmail(userDto.getEmail());

        log.info("Creating user with username: {}, email: {}, role: {}", user.getUsername(), user.getEmail(), user.getRole());
        return userRepository.save(user);
    }
}







