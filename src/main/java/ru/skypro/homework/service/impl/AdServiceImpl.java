package ru.skypro.homework.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.dto.AdDTO;
import ru.skypro.homework.mapper.AdMapper;
import ru.skypro.homework.model.Ad;
import ru.skypro.homework.repository.AdRepository;
import ru.skypro.homework.service.AdService;

import javax.transaction.Transactional;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Service
public class AdServiceImpl implements AdService {

    private final AdRepository adRepository;
    private final AdMapper adMapper;

    @Autowired
    public AdServiceImpl(AdRepository adRepository, AdMapper adMapper) {
        this.adRepository = adRepository;
        this.adMapper = adMapper;
    }

    // Получение всех объявлений
    @Override
    public List<AdDTO> getAllAds() {
        return adMapper.toDTOList(adRepository.findAll());
    }

    // Добавление объявления
    @Override
    public AdDTO addAd(AdDTO adDTO) {
        Ad ad = adMapper.toEntity(adDTO);
        Ad savedAd = adRepository.save(ad);
        return adMapper.toDTO(savedAd);
    }

    // Получение объявления по ID
    @Override
    public AdDTO getAdById(Integer id) {
        return adRepository.findById(id)
                .map(adMapper::toDTO)
                .orElseThrow(() -> new RuntimeException("Ad not found"));
    }

    // Удаление объявления
    @Override
    @Transactional
    public void deleteAd(Integer id) {
        adRepository.deleteById(id);
    }

    // Обновление информации об объявлении
    @Override
    @Transactional
    public AdDTO updateAd(Integer id, AdDTO adDTO) {
        Ad ad = adRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ad not found"));
        ad.setTitle(adDTO.getTitle());
        ad.setPrice(adDTO.getPrice());
        ad.setImage(adDTO.getImage());
        // Обновите другие поля при необходимости
        Ad updatedAd = adRepository.save(ad);
        return adMapper.toDTO(updatedAd);
    }

    // Получение объявлений авторизованного пользователя
    @Override
    public List<AdDTO> getAdsByAuthor(String username) {
        // Предполагается, что у вас есть метод в репозитории для поиска по автору
        return adMapper.toDTOList(adRepository.findByUserEmail(username));
    }

    // Обновление картинки объявления
    @Override
    @Transactional
    public AdDTO updateAdImage(Integer id, MultipartFile image) {
        Ad ad = adRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ad not found"));
        try {
            String imagePath = saveImage(image); // Реализуйте этот метод
            ad.setImage(imagePath);
            Ad updatedAd = adRepository.save(ad);
            return adMapper.toDTO(updatedAd);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save image", e);
        }
    }

    private String saveImage(MultipartFile image) throws IOException {
        // Пример реализации сохранения изображения
        String directory = "path/to/images"; // Укажите реальный путь к директории для сохранения
        if (!new File(directory).exists()) {
            new File(directory).mkdirs(); // Создание директории, если она не существует
        }
        String fileName = System.currentTimeMillis() + "_" + image.getOriginalFilename();
        Path filePath = Paths.get(directory, fileName);
        Files.copy(image.getInputStream(), filePath);
        return filePath.toString(); // Возвращаем путь к изображению
    }
}



