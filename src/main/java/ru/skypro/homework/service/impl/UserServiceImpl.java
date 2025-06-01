package ru.skypro.homework.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.dto.*;
import ru.skypro.homework.exception.InvalidCurrentPasswordException;
import ru.skypro.homework.exception.InvalidPasswordException;
import ru.skypro.homework.exception.UserNotFoundException;
import ru.skypro.homework.mapper.UserMapper;
import ru.skypro.homework.model.Image;
import ru.skypro.homework.model.User;
import ru.skypro.homework.repository.ImageRepository;
import ru.skypro.homework.repository.UserRepository;
import ru.skypro.homework.service.CurrentUserService;
import ru.skypro.homework.service.ImageService;
import ru.skypro.homework.service.ImageStorageService;
import ru.skypro.homework.service.UserService;

import javax.persistence.EntityNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final CurrentUserService currentUserService;
    private final ImageService imageService;
    private final ImageRepository imageRepository;
    private final ImageStorageService imageStorageService;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final Path uploadDir;

    public Path getUploadDir() {
        return uploadDir;
    }


    @Override
    public UserDTO getCurrentUser () {
        User user = currentUserService.getAuthenticatedUser ();
        if (user != null) {
            return userMapper.toDTO(user);
        }
        throw new UsernameNotFoundException("Пользователь не найден");
    }

    @Transactional
    @Override
    public UserDTO updateUser (UpdateUserDTO updateUserDTO) {
        User user = currentUserService.getAuthenticatedUser ();
        log.info("Обновление пользователя: {}", user.getEmail());

        userMapper.updateEntityFromDTO(updateUserDTO, user);
        userRepository.save(user);
        log.info("Пользователь {} успешно обновлён", user.getEmail());
        return userMapper.toDTO(user);
    }




    @Transactional
    @Override
    public void updateUserImage(MultipartFile file) throws IOException {
        User user = currentUserService.getAuthenticatedUser ();
        log.info("Пользователь {} пытается обновить аватар", user.getId());

        String imageUrl = imageStorageService.store(file);
        Image avatar = new Image();
        avatar.setImageUrl(imageUrl);
        avatar.setUser (user);

        imageRepository.save(avatar);
        user.setAvatar(avatar);
        userRepository.save(user);

        log.info("Пользователь {} успешно обновил аватар", user.getId());
    }

    @Override
    public List<UserDTO> getAllUsers() {
        List<User> users = userRepository.findAll();
        log.info("Получение всех пользователей. Всего пользователей: {}", users.size());
        return userMapper.toDTOList(users);
    }

    @Transactional
    @Override
    public void setPassword(NewPasswordDTO newPassword) {
        User currentUser  = currentUserService.getAuthenticatedUser ();
        if (currentUser  == null) {
            log.error("Пользователь не найден при смене пароля");
            throw new UserNotFoundException("Пользователь не найден");
        }

        log.info("Аутентифицированный пользователь: {}", currentUser );

        // Логируем email для проверки
        String email = currentUser .getEmail();
        log.info("Запрос на получение пользователя с email: {}", email);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("Пользователь не найден"));

        // Проверяем текущий пароль
        if (!passwordEncoder.matches(newPassword.getCurrentPassword(), user.getPassword())) {
            log.error("Текущий пароль неверен для пользователя: {}", user.getEmail());
            throw new InvalidCurrentPasswordException("Текущий пароль неверен");
        }

        // Валидация нового пароля
        validatePasswordComplexity(newPassword.getNewPassword());
        log.info("Новый пароль прошел валидацию для пользователя: {}", user.getEmail());

        // Устанавливаем новый закодированный пароль
        String encodedPassword = passwordEncoder.encode(newPassword.getNewPassword());
        user.setPassword(encodedPassword);

        try {
            userRepository.save(user);
            log.info("Пароль для пользователя {} успешно изменён", user.getEmail());
        } catch (DataAccessException e) {
            log.error("Ошибка при сохранении пользователя {}: {}", user.getEmail(), e.getMessage());
            throw new RuntimeException("Не удалось изменить пароль пользователя", e);
        }
    }








    private void validatePasswordComplexity(String password) {
        if (password.length() < 8) {
            throw new InvalidPasswordException("Пароль должен содержать не менее 8 символов");
        }
        if (!password.matches(".*\\d.*")) {
            throw new InvalidPasswordException("Пароль должен содержать хотя бы одну цифру");
        }
        if (!password.matches(".*[A-Z].*")) {
            throw new InvalidPasswordException("Пароль должен содержать хотя бы одну заглавную букву");
        }
        if (!password.matches(".*[!@#$%^&*()].*")) {
            throw new InvalidPasswordException("Пароль должен содержать хотя бы один специальный символ");
        }
    }


    @Override
    public User createUser(UserDTO userDto) {
        if (userRepository.existsByEmail(userDto.getEmail())) {
            log.warn("Пользователь с email {} уже существует", userDto.getEmail());
            throw new IllegalArgumentException("Email already exists");
        }

        User user = new User();
        user.setEmail(userDto.getEmail());
        user.setPassword(passwordEncoder.encode(userDto.getPassword())); // Храните хешированный пароль
        user.setFirstName(userDto.getFirstName());
        user.setLastName(userDto.getLastName());
        user.setPhone(userDto.getPhone());
        user.setRole(userDto.getRole());

        log.info("Создание пользователя с email: {}, роль: {}", user.getEmail(), user.getRole());
        return userRepository.save(user);
    }

    @Transactional(readOnly = true)
    @Override
    public UserDTO getUserWithComments(Integer userId) {
        User user = userRepository.findByIdWithComments(userId);
        if (user == null) {
            throw new EntityNotFoundException("User not found");
        }

        log.info("Получение пользователя с ID: {}", userId);

        List<CommentDTO> commentDTOs = user.getComments().stream()
                .map(comment -> {
                    CommentDTO dto = new CommentDTO();
                    dto.setAuthor(comment.getUser().getId());
                    dto.setAuthorImage(comment.getUser().getAvatar() != null ? comment.getUser().getAvatar().getUrl() : null);
                    dto.setAuthorFirstName(comment.getUser().getFirstName());
                    dto.setCreatedAt(comment.getCreatedAt());
                    dto.setPk(comment.getPk());
                    dto.setText(comment.getText());
                    return dto;
                })
                .collect(Collectors.toList());

        UserDTO userDTO = userMapper.toDTO(user);
        userDTO.setComments(commentDTOs);

        log.info("Пользователь с ID {} успешно получен с {} комментариями", userId, commentDTOs.size());
        return userDTO;
    }

    @Override
    public String getUserAvatar(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Пользователь с ID " + userId + " не найден"));

        log.info("Получение аватара для пользователя с ID: {}", userId);
        Image avatar = user.getAvatar(); // Предполагается, что у пользователя есть поле avatar, которое ссылается на Image

        if (avatar != null) {
            log.info("Аватар пользователя с ID {} получен: {}", userId, avatar.getImageUrl());
            return avatar.getImageUrl(); // Возвращаем URL аватара
        } else {
            log.warn("Аватар для пользователя с ID {} не найден", userId);
            return null; // Или можно вернуть какое-то значение по умолчанию
        }
    }

    @Override
    @Transactional
    public void updateImageForUser (Integer currentUserId, MultipartFile file) {
        User user = userRepository.findById(currentUserId)
                .orElseThrow(() -> new UserNotFoundException("Пользователь с ID " + currentUserId + " не найден"));

        log.info("Пользователь {} обновляет аватар", user.getEmail());

        try {
            // Сохраняем файл через ImageStorageService и получаем URL
            String imageUrl = imageStorageService.store(file);

            // Создаем новый объект Image и связываем его с пользователем
            Image avatar = new Image();
            avatar.setImageUrl(imageUrl);
            avatar.setUser (user);

            // Сохраняем аватар в базе данных
            imageRepository.save(avatar);

            // Обновляем пользователя с новым аватаром
            user.setAvatar(avatar);
            userRepository.save(user);

            log.info("Аватар для пользователя {} успешно обновлён: {}", user.getEmail(), imageUrl);
        } catch (IOException e) {
            log.error("Ошибка при обновлении аватара для пользователя {}: {}", user.getEmail(), e.getMessage());
            throw new RuntimeException("Не удалось обновить аватар", e);
        }
    }

}










