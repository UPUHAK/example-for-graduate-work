package ru.skypro.homework.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.dto.NewPasswordDTO;
import ru.skypro.homework.dto.UpdateUserDTO;
import ru.skypro.homework.dto.UserDTO;
import ru.skypro.homework.mapper.UserMapper;
import ru.skypro.homework.model.User;
import ru.skypro.homework.repository.UserRepository;
import ru.skypro.homework.service.CurrentUserService;
import ru.skypro.homework.service.UserService;

import java.util.List;
@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ImageServiceImpl avatarStorageService;
    private final UserMapper userMapper; // Внедряем UserMapper
    private final CurrentUserService currentUserService; // Внедряем CurrentUser Service

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    @Override
    public UserDTO getCurrentUser() {
        User user = currentUserService.getCurrentUser(); // Используем метод из CurrentUser Service
        return userMapper.toDTO(user); // Преобразуем в DTO
    }

    @Override
    public UserDTO updateUser(UpdateUserDTO updateUserDTO) {
        User user = currentUserService.getCurrentUser(); // Получаем текущего пользователя
        userMapper.updateEntityFromDTO(updateUserDTO, user); // Обновляем сущность
        userRepository.save(user); // Сохраняем изменения
        return userMapper.toDTO(user); // Возвращаем обновленный UserDTO
    }

    @Override
    public List<UserDTO> getAllUsers() {
        List<User> users = userRepository.findAll(); // Получаем всех пользователей
        return userMapper.toDTOList(users); // Преобразуем список пользователей в список DTO
    }

    @Override
    public void setPassword(NewPasswordDTO newPassword) {
        User user = currentUserService.getCurrentUser(); // Получаем текущего пользователя
        user.setPassword(passwordEncoder.encode(newPassword.getNewPassword())); // Кодируем новый пароль
        userRepository.save(user); // Сохраняем изменения
    }

    @Override
    public void updateAvatar(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Файл не может быть пустым");
        }

        User user = currentUserService.getCurrentUser();

        try {
            avatarStorageService.saveAvatar(user, file);
        } catch (Exception e) {
            logger.error("Ошибка при обновлении аватара для пользователя {}: {}", user.getId(), e.getMessage());
            throw new RuntimeException("Не удалось обновить аватар", e);
        }
    }


    // Метод для проверки допустимых типов изображений
    // private boolean isValidImageType(String contentType) {
    //    return contentType != null && (contentType.equals("image/jpeg") || contentType.equals("image/png"));
   // }


}




