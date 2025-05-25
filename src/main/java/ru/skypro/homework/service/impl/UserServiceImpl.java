package ru.skypro.homework.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


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
    public void updateImage(Integer userId, MultipartFile file) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Пользователь с ID " + userId + " не найден"));


        if (file.isEmpty()) {
            log.warn("Пользователь {} попытался загрузить пустой файл.", user.getId());
            throw new IllegalArgumentException("Ошибка: файл пустой!");
        }

        ImageDTO imageDTO = new ImageDTO();
        imageDTO.setUserId(user.getId().intValue());
        imageDTO.setImageUrl(file.getOriginalFilename());


        try {
            imageDTO.setData(file.getBytes());
        } catch (IOException e) {
            log.error("Ошибка чтения файла для пользователя {}: {}", user.getId(), e.getMessage());
            throw new RuntimeException("Ошибка при чтении файла. Попробуйте снова.", e);
        }
        imageDTO.setAdId(null);


        try {
            String originalFilename = StringUtils.cleanPath(file.getOriginalFilename());
            Path imagePath = Paths.get("path/to/save/" + originalFilename);


            Files.createDirectories(imagePath.getParent());

            imageService.saveToDatabase(imageDTO, imagePath, file);
            log.info("Пользователь {} успешно обновил аватар: {}", user.getId(), originalFilename);
        } catch (EntityNotFoundException e) {
            log.error("Ошибка сохранения: пользователь не найден: {}", e.getMessage());
            throw new RuntimeException("Пользователь не найден.", e);
        } catch (IOException e) {
            log.error("Ошибка при сохранении файла для пользователя {}: {}", user.getId(), e.getMessage());
            throw new RuntimeException("Ошибка при сохранении файла. Попробуйте снова.", e);
        }
    }


    @Override
    public void updateImage(MultipartFile file) {

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






// Метод для проверки допустимых типов изображений
// private boolean isValidImageType(String contentType) {
//    return contentType != null && (contentType.equals("image/jpeg") || contentType.equals("image/png"));
// }








