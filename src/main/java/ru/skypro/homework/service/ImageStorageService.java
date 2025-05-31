package ru.skypro.homework.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class ImageStorageService {

    // Папка для хранения изображений
    private final Path rootLocation = Paths.get("uploads/images");

    public ImageStorageService() {
        // Создание директории, если она не существует
        try {
            Files.createDirectories(rootLocation);
        } catch (IOException e) {
            throw new RuntimeException("Не удалось создать директорию для хранения изображений", e);
        }
    }

    public String store(MultipartFile file) throws IOException {
        // Проверка на пустой файл
        if (file.isEmpty()) {
            throw new IllegalArgumentException("Файл не должен быть пустым");
        }

        // Проверка типа файла
        String contentType = file.getContentType();
        if (contentType == null || (!contentType.equals("image/jpeg") && !contentType.equals("image/png"))) {
            throw new IllegalArgumentException("Только файлы JPEG и PNG поддерживаются");
        }

        // Генерация уникального имени файла
        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename != null && originalFilename.contains(".")
                ? originalFilename.substring(originalFilename.lastIndexOf("."))
                : "";
        String uniqueFilename = UUID.randomUUID().toString() + extension;

        // Полный путь к файлу
        Path destinationFile = rootLocation.resolve(uniqueFilename);

        // Создание директории, если она не существует
        Files.createDirectories(destinationFile.getParent());

        // Сохранение файла
        try {
            Files.copy(file.getInputStream(), destinationFile, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new IOException("Ошибка при сохранении файла: " + e.getMessage(), e);
        }

        // Возврат относительного пути к файлу
        return uniqueFilename;
    }


    public void delete(String imageUrl) {
        Path fileToDelete = rootLocation.resolve(imageUrl);
        try {
            Files.deleteIfExists(fileToDelete);
        } catch (IOException e) {
            throw new RuntimeException("Ошибка при удалении файла", e);
        }
    }



}

