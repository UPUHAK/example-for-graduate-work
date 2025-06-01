package ru.skypro.homework.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.model.Image;
import ru.skypro.homework.repository.UserRepository;
import ru.skypro.homework.service.AvatarService;
import ru.skypro.homework.service.ImageService;

import javax.persistence.EntityNotFoundException;
import java.io.IOException;

@Service
public class AvatarServiceImpl implements AvatarService {

    private final ImageService imageService;
    private final UserRepository userRepository;

    @Autowired
    public AvatarServiceImpl(ImageService imageService, UserRepository userRepository) {
        this.imageService = imageService;
        this.userRepository = userRepository;
    }

    @Override
    public Image updateAvatar(Integer userId, MultipartFile file) throws IOException, EntityNotFoundException {
        // Проверяем, что пользователь существует
        userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь с ID " + userId + " не найден"));

        // Используем метод updateImageForUser из ImageService для обновления аватара
        return imageService.updateImageForUser(userId, file);
    }
}

