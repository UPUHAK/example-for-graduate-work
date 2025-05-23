package ru.skypro.homework.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.exception.ImageNotFoundException;
import ru.skypro.homework.exception.ResourceNotFoundException;
import ru.skypro.homework.model.Image;
import ru.skypro.homework.model.User;
import ru.skypro.homework.repository.ImageRepository;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import ru.skypro.homework.model.Image;
import ru.skypro.homework.repository.ImageRepository;
import ru.skypro.homework.service.ImageService;

import java.util.List;
import java.util.Optional;

@Service
public class ImageServiceImpl implements ImageService {

    private final ImageRepository imageRepository;

    public ImageServiceImpl(ImageRepository imageRepository) {
        this.imageRepository = imageRepository;
    }

    @Override
    public Image saveAvatar(User user, MultipartFile file) {
        // Логика сохранения аватара
        return null; // Замените на вашу реализацию
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
            // Обновите необходимые поля
            imageToUpdate.setImageUrl(imageDetails.getImageUrl());
            imageToUpdate.setImageType(imageDetails.getImageType());
            return imageRepository.save(imageToUpdate);
        }
        throw new ImageNotFoundException(id); // Выбрасываем исключение, если изображение не найдено
    }

    @Override
    public void deleteImage(Integer id) {
        imageRepository.deleteById(id);
    }
}

