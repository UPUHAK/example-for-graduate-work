package ru.skypro.homework.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.model.Ad;
import ru.skypro.homework.model.Image;
import ru.skypro.homework.repository.AdRepository;
import ru.skypro.homework.repository.ImageRepository;
import ru.skypro.homework.service.ImageAdService;
import ru.skypro.homework.service.ImageService;
import ru.skypro.homework.service.ImageStorageService;

import javax.persistence.EntityNotFoundException;
import java.io.IOException;

@Service
public class ImageAdServiceImpl implements ImageAdService {

    private final ImageStorageService imageStorageService;
    private final AdRepository adRepository;
    private final ImageRepository imageRepository;

    @Autowired
    public ImageAdServiceImpl(ImageStorageService imageStorageService, AdRepository adRepository, ImageRepository imageRepository) {
        this.imageStorageService = imageStorageService;
        this.adRepository = adRepository;
        this.imageRepository = imageRepository;
    }

    @Override
    public Image saveAdImage(Integer adId, MultipartFile file) throws IOException {
        // Проверяем, что объявление существует
        Ad ad = adRepository.findById(adId)
                .orElseThrow(() -> new EntityNotFoundException("Объявление с ID " + adId + " не найдено"));

        // Сохраняем файл через ImageStorageService
        String filename = imageStorageService.store(file);

        // Создаём и сохраняем объект Image с ссылкой на файл и объявление
        Image image = new Image();
        image.setAd(ad); // Устанавливаем объявление
        image.setImageUrl(filename); // Устанавливаем URL изображения


        imageRepository.save(image);

        return image;
    }

}


