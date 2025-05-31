package ru.skypro.homework.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


import org.hibernate.Hibernate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import ru.skypro.homework.dto.*;

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
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Optional;
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
    public void updateImage(MultipartFile file) throws IOException {
        User user = currentUserService.getAuthenticatedUser (); // Получаем авторизованного пользователя
        log.info("Пользователь {} пытается обновить аватар", user.getId());

        validateFile(file); // Проверяем файл
        String imageUrl = imageStorageService.store(file); // Сохраняем изображение и получаем URL

        // Обновляем или создаём запись Image с ссылкой на файл
        Image image = imageRepository.findByUserId(user.getId())
                .orElseGet(() -> new Image(user));
        image.setImageUrl(imageUrl);
        imageRepository.save(image);

        log.info("Пользователь {} успешно обновил аватар: {}", user.getId(), imageUrl);
    }

    private void validateFile(MultipartFile file) {
        if (file.isEmpty()) {
            log.warn("Попытка загрузки пустого файла.");
            throw new IllegalArgumentException("Ошибка: файл пустой!");
        }

        String contentType = file.getContentType();
        if (!"image/jpeg".equals(contentType) && !"image/png".equals(contentType)) {
            log.warn("Неподдерживаемый тип файла: {}", contentType);
            throw new IllegalArgumentException("Ошибка: поддерживаются только JPEG и PNG файлы.");
        }

        if (file.getSize() > 2 * 1024 * 1024) { // 2 МБ
            log.warn("Размер файла превышает допустимый лимит.");
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

    public void saveImageToFileSystem(MultipartFile file, ImageDTO imageDTO) {
        try {
            String originalFilename = StringUtils.cleanPath(file.getOriginalFilename());
            Path directory = Paths.get("C:\\Users\\пк\\Pictures\\Images");
            Files.createDirectories(directory);

            Path imagePath = directory.resolve(originalFilename);

            // Сохраняем файл на диск
            Files.copy(file.getInputStream(), imagePath, StandardCopyOption.REPLACE_EXISTING);

            // Обновляем или создаём запись Image с ссылкой на файл
            Optional<Image> optionalImage = imageRepository.findByUserId(imageDTO.getUserId());

            Image image = optionalImage.orElseGet(() -> {
                User user = userRepository.findById(imageDTO.getUserId())
                        .orElseThrow(() -> new EntityNotFoundException("Пользователь с ID " + imageDTO.getUserId() + " не найден"));
                Image newImage = new Image();
                newImage.setUser (user);
                return newImage;
            });

            image.setImageUrl(imagePath.toString());
            imageRepository.save(image);

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
    public void saveImageFromFilePath(String filePath, ImageDTO imageDTO) {
        // Реализация метода
        log.info("Сохранение изображения по пути: {}", filePath);
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
        user.setPassword(userDto.getPassword());
        user.setFirstName(userDto.getFirstName());
        user.setLastName(userDto.getLastName());
        user.setPhone(userDto.getPhone());
        user.setRole(userDto.getRole());

        log.info("Создание пользователя с email: {}, роль: {}", user.getEmail(), user.getRole());
        return userRepository.save(user);
    }

    @Transactional
    public UserDTO getUserWithComments(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User  not found"));

        log.info("Получение пользователя с ID: {}", userId);
        // Инициализация коллекции комментариев, если необходимо
        Hibernate.initialize(user.getComments());

        List<CommentDTO> commentDTOs = user.getComments().stream()
                .map(comment -> {
                    CommentDTO dto = new CommentDTO();
                    dto.setAuthor(comment.getUser ().getId());
                    dto.setAuthorImage(comment.getUser ().getAvatar() != null ? comment.getUser ().getAvatar().getUrl() : null);
                    dto.setAuthorFirstName(comment.getUser ().getFirstName());
                    dto.setCreatedAt(comment.getCreatedAt());
                    dto.setPk(comment.getPk());
                    dto.setText(comment.getText());
                    return dto;
                })
                .collect(Collectors.toList());

        UserDTO userDTO = new UserDTO();
        userDTO.setId(user.getId());
        userDTO.setEmail(user.getEmail());
        userDTO.setFirstName(user.getFirstName());
        userDTO.setLastName(user.getLastName());
        userDTO.setPhone(user.getPhone());
        userDTO.setRole(user.getRole());
        userDTO.setAvatar(user.getAvatar() != null ? user.getAvatar().getUrl() : null);
        userDTO.setComments(commentDTOs);

        log.info("Пользователь с ID {} успешно получен с {} комментариями", userId, commentDTOs.size());
        return userDTO;
    }
}








