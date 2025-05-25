package ru.skypro.homework.service;

import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.dto.NewPasswordDTO;
import ru.skypro.homework.dto.UpdateUserDTO;
import ru.skypro.homework.dto.UserDTO;
import ru.skypro.homework.model.User;

import java.util.List;

public interface UserService {

    /**
     * Устанавливает новый пароль для пользователя.
     * @param newPassword DTO с именем пользователя и новым паролем
     */
    void setPassword(NewPasswordDTO newPassword);

    /**
     * Возвращает DTO текущего аутентифицированного пользователя.
     * @return UserDTO с информацией о пользователе
     */
    UserDTO getCurrentUser();

    /**
     * Обновляет данные текущего пользователя.
     * @param updateUser DTO с обновлёнными данными пользователя
     * @return обновлённый UserDTO
     */
    UserDTO updateUser(UpdateUserDTO updateUser);

    /**
     * Обновляет аватар текущего пользователя.
     * @param file файл с изображением аватара
     */
    void updateImage(MultipartFile file);

    /**
     * Возвращает список всех пользователей.
     * @return список UserDTO с информацией о пользователях
     */
    List<UserDTO> getAllUsers();

    User createUser(UserDTO userDto);
}



