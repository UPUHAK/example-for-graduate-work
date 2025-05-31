package ru.skypro.homework.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.dto.ImageDTO;
import ru.skypro.homework.exception.ImageNotFoundException;
import ru.skypro.homework.mapper.ImageMapper;
import ru.skypro.homework.model.Image;
import ru.skypro.homework.model.User;
import ru.skypro.homework.repository.ImageRepository;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Optional;

import ru.skypro.homework.repository.UserRepository;
import ru.skypro.homework.service.ImageService;
import ru.skypro.homework.service.ImageStorageService;

import javax.persistence.EntityNotFoundException;



@Service
public class ImageServiceImpl implements ImageService {

    private final ImageRepository imageRepository;
    private final ImageMapper imageMapper;
    private final UserRepository userRepository;
    private final ImageStorageService imageStorageService;

    private static final Logger log = LoggerFactory.getLogger(ImageServiceImpl.class);

    @Autowired
    public ImageServiceImpl(ImageRepository imageRepository, ImageMapper imageMapper, UserRepository userRepository, ImageStorageService imageStorageService) {
        this.imageRepository = imageRepository;
        this.imageMapper = imageMapper;
        this.userRepository = userRepository;
        this.imageStorageService = imageStorageService;
    }

    public ImageDTO convertToDto(Image image) {
        return imageMapper.imageToImageDTO(image);
    }

    public Image convertToEntity(ImageDTO imageDTO) {
        return imageMapper.imageDTOToImage(imageDTO);
    }

    @Override
    public Optional<Image> findImageById(Integer id) {
        return imageRepository.findById(id);
    }

    @Override
    @Transactional
    public Image saveImage(Integer userId, MultipartFile file) throws IOException {
        // Проверка на пустой файл
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Файл не должен быть пустым");
        }

        // Поиск пользователя по ID
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь с ID " + userId + " не найден"));

        // Сохранение файла в файловой системе
        String imageUrl;
        try {
            imageUrl = imageStorageService.store(file); // Пытаемся сохранить файл
        } catch (IOException e) {
            log.error("Ошибка при сохранении файла для пользователя {}: {}", userId, e.getMessage());
            throw new RuntimeException("Ошибка при сохранении файла. Попробуйте снова.", e);
        }

        // Создание и сохранение объекта Image
        Image image = new Image();
        image.setUser (user);
        image.setImageUrl(imageUrl);

        return imageRepository.save(image);
    }

    @Override
    public Optional<Image> getImageById(Integer id) {
        return imageRepository.findById(id);
    }

    @Override
    public List<Image> getAllImages() {
        return imageRepository.findAll();
    }

    @Override
    public Image createImage(Image image) {
        return imageRepository.save(image);
    }

    @Override
    public Image updateImage(Integer id, MultipartFile file) {
        Optional<Image> existingImage = imageRepository.findById(id);
        if (existingImage.isPresent()) {
            Image imageToUpdate = existingImage.get();

            // Логика для обработки файла и получения URL
            String imageUrl = saveImageToFileSystem(file); // Метод для сохранения файла и получения URL
            imageToUpdate.setImageUrl(imageUrl);

            return imageRepository.save(imageToUpdate);
        }
        throw new ImageNotFoundException(id);
    }


    @Override
    public void deleteImage(Integer id) {
        if (!imageRepository.existsById(id)) {
            throw new EntityNotFoundException("Изображение с ID " + id + " не найдено.");
        }

        // Получаем изображение для удаления, чтобы удалить файл из файловой системы
        Image image = imageRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Изображение не найдено"));
        imageStorageService.delete(image.getImageUrl()); // Удаляем файл из файловой системы
        imageRepository.deleteById(id); // Удаляем запись из базы данных
    }

    @Override
    @Transactional
    public Image updateImageForUser (Integer userId, MultipartFile file) throws IOException, EntityNotFoundException {
        // Проверка на пустой файл
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Ошибка: файл пустой!");
        }

        // Поиск пользователя по ID
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь не найден."));

        // Сохранение изображения через уже существующий метод
        Image savedImage = saveImage(userId, file); // Используем метод saveImage для сохранения

        // Обновление пути к изображению у пользователя
        user.setImage(savedImage.getImageUrl());
        userRepository.save(user); // Сохраняем изменения в базе данных

        return savedImage; // Возвращаем сохраненное изображение
    }
    private String saveImageToFileSystem(MultipartFile file) {
        // Укажите путь, куда вы хотите сохранить изображение
        String uploadDir = "/path/to/upload/directory/";
        String fileName = file.getOriginalFilename();
        Path filePath = Paths.get(uploadDir + fileName);

        try {
            // Сохранение файла
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            return "/images/" + fileName; // Верните URL к изображению (или измените по вашему усмотрению)
        } catch (IOException e) {
            throw new RuntimeException("Не удалось сохранить файл: " + e.getMessage());
        }
    }
    @Override
    @Transactional
    public Image updateImage(Integer id, Image imageDetails) {
        // Находим существующее изображение по ID
        Image existingImage = imageRepository.findById(id)
                .orElseThrow(() -> new ImageNotFoundException(id)); // Исключение, если изображение не найдено

        // Обновляем поля существующего изображения
        if (imageDetails.getImageUrl() != null) {
            existingImage.setImageUrl(imageDetails.getImageUrl());
        }
        // Добавьте другие поля, которые нужно обновить, например:
        // if (imageDetails.getSomeOtherField() != null) {
        //     existingImage.setSomeOtherField(imageDetails.getSomeOtherField());
        // }

        // Сохраняем обновлённое изображение
        return imageRepository.save(existingImage);
    }



}








