package ru.skypro.homework.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Hibernate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.dto.*;
import ru.skypro.homework.exception.UserNotFoundException;
import ru.skypro.homework.mapper.UserMapper;
import ru.skypro.homework.model.Image;
import ru.skypro.homework.model.User;
import ru.skypro.homework.repository.ImageRepository;
import ru.skypro.homework.repository.UserRepository;
import ru.skypro.homework.service.CurrentUserService;
import ru.skypro.homework.service.CurrentUserService;
import ru.skypro.homework.service.ImageService;
import ru.skypro.homework.service.ImageStorageService;
import ru.skypro.homework.service.UserService;

import javax.persistence.EntityNotFoundException;
import java.io.IOException;
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

    @Override
    public UserDTO getCurrentUser () {
        User user = currentUserService.getAuthenticatedUser ();
        log.info("Получение текущего пользователя: {}", user.getEmail());
        return userMapper.toDTO(user);
    }

    @Override
    public UserDTO updateUser (UpdateUserDTO updateUserDTO) {
        User user = currentUserService.getAuthenticatedUser ();
        log.info("Обновление пользователя: {}", user.getEmail());
        userMapper.updateEntityFromDTO(updateUserDTO, user);
        userRepository.save(user);
        log.info("Пользователь {} успешно обновлён", user.getEmail());
        return userMapper.toDTO(user);
    }

    @Override
    @Transactional
    public void updateImage(MultipartFile file) throws IOException {
        User user = currentUserService.getAuthenticatedUser (); // Получаем авторизованного пользователя
        log.info("Пользователь {} пытается обновить аватар", user.getId());

        imageService.updateImageForUser (user.getId(), file); // Используем метод из ImageService

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
        User user = currentUserService.getAuthenticatedUser ();
        log.info("Смена пароля для пользователя: {}", user.getEmail());
        user.setPassword(passwordEncoder.encode(newPassword.getNewPassword()));
        userRepository.save(user);
        log.info("Пароль для пользователя {} успешно изменён", user.getEmail());
    }

    @Override
    public User createUser (UserDTO userDto) {
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
    public void updateImageForUser(Integer currentUserId, MultipartFile file) {

    }
}










