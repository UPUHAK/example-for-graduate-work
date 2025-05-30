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
import java.util.UUID;

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

        try {
            return imageRepository.save(image);
        } catch (Exception e) {
            // Если сохранение в БД не удалось, удаляем файл из файловой системы
            imageStorageService.delete(imageUrl);
            log.error("Ошибка при сохранении изображения в базе данных для пользователя {}: {}", userId, e.getMessage());
            throw new RuntimeException("Ошибка при сохранении данных в базе. Попробуйте снова.", e);
        }
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
    public Image updateImage(Integer id, Image imageDetails) {
        Optional<Image> existingImage = imageRepository.findById(id);
        if (existingImage.isPresent()) {
            Image imageToUpdate = existingImage.get();
            imageToUpdate.setImageUrl(imageDetails.getImageUrl());
            return imageRepository.save(imageToUpdate);
        }
        throw new ImageNotFoundException(id);
    }

    @Override
    public void deleteImage(Integer id) {
        if (!imageRepository.existsById(id)) {
            throw new EntityNotFoundException("Изображение с ID " + id + " не найдено.");
        }

        imageRepository.deleteById(id);
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
    @Override
    public String saveFile(MultipartFile file) throws IOException {
        // Проверяем, что файл не пустой
        if (file.isEmpty()) {
            throw new IllegalArgumentException("Файл пустой");
        }

        // Получаем оригинальное имя файла
        String originalFilename = file.getOriginalFilename();

        // Путь для сохранения файла (пример)
        Path uploadPath = Paths.get("uploads/images");
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath); // Создаем директории, если они не существуют
        }

        // Генерируем уникальное имя файла, чтобы избежать конфликтов
        String filename = UUID.randomUUID() + "_" + originalFilename;

        // Сохраняем файл
        Path filePath = uploadPath.resolve(filename);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        // Возвращаем путь или имя файла для дальнейшего использования
        return filePath.toString(); // Можно вернуть относительный путь или имя файла
    }
}








