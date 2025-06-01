package ru.skypro.homework.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.exception.FileStorageException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class ImageStorageService {

    private static final Logger logger = LoggerFactory.getLogger(ImageStorageService.class);
    private final Path rootLocation = Paths.get("uploads/images");

    public ImageStorageService() {
        try {
            Files.createDirectories(rootLocation);
        } catch (IOException e) {
            logger.error("Не удалось создать директорию для хранения изображений: {}", e.getMessage());
            throw new RuntimeException("Не удалось создать директорию для хранения изображений", e);
        }
    }

    public String store(MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new FileStorageException("Файл не должен быть пустым");
        }

        String contentType = file.getContentType();
        if (contentType == null || (!contentType.equals("image/jpeg") && !contentType.equals("image/png"))) {
            throw new FileStorageException("Только файлы JPEG и PNG поддерживаются");
        }

        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename != null && originalFilename.contains(".")
                ? originalFilename.substring(originalFilename.lastIndexOf("."))
                : "";
        String uniqueFilename = UUID.randomUUID().toString() + extension;

        Path destinationFile = rootLocation.resolve(uniqueFilename);

        try {
            // Проверка существования файла
            if (Files.exists(destinationFile)) {
                throw new FileStorageException("Файл с таким именем уже существует");
            }
            Files.copy(file.getInputStream(), destinationFile, StandardCopyOption.REPLACE_EXISTING);
            logger.info("Файл успешно сохранён: {}", uniqueFilename);
        } catch (IOException e) {
            logger.error("Ошибка при сохранении файла: {}", e.getMessage());
            throw new FileStorageException("Ошибка при сохранении файла: " + e.getMessage(), e);
        }

        return "/images/" + uniqueFilename;
    }

    public void delete(String imageUrl) {
        Path fileToDelete = rootLocation.resolve(imageUrl.substring(7)); // Убираем "/images/"
        try {
            Files.deleteIfExists(fileToDelete);
            logger.info("Файл успешно удалён: {}", fileToDelete);
        } catch (IOException e) {
            logger.error("Ошибка при удалении файла: {}", e.getMessage());
            throw new RuntimeException("Ошибка при удалении файла", e);
        }
    }
}






