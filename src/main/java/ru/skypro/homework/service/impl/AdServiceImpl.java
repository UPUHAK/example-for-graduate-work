package ru.skypro.homework.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Value;
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
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdServiceImpl implements AdService {

    @Value("${image.upload.dir}")
    private String uploadDir;

    private final AdRepository adRepository;
    private final AdMapper adMapper;

    @Autowired
    public AdServiceImpl(AdRepository adRepository, AdMapper adMapper) {
        this.adRepository = adRepository;
        this.adMapper = adMapper;
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
        return adMapper.toDTOList(adRepository.findByUserEmail(username));
    }

    // Обновление картинки объявления
    @Override
    @Transactional
    public AdDTO updateAdImage(Integer id, MultipartFile image) {
        Ad ad = adRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ad not found"));

        // Проверка, что файл - изображение
        String contentType = image.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException("Uploaded file is not an image");
        }

        try {
            String imagePath = saveImage(image);
            ad.setImage(imagePath);
            Ad updatedAd = adRepository.save(ad);
            return adMapper.toDTO(updatedAd);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save image", e);
        }
    }

    private String saveImage(MultipartFile image) throws IOException {
        File dir = new File(uploadDir);
        if (!dir.exists() && !dir.mkdirs()) {
            throw new IOException("Could not create upload directory: " + uploadDir);
        }

        String fileName = System.currentTimeMillis() + "_" + image.getOriginalFilename();
        Path destinationPath = Paths.get(uploadDir).resolve(fileName).normalize();

        // Копируем файл, перезаписывая, если уже существует
        Files.copy(image.getInputStream(), destinationPath, StandardCopyOption.REPLACE_EXISTING);

        // Возвращаем относительный путь, например: "images/123456_filename.jpg"
        return fileName;
    }
    @Override
    public List<AdDTO> getAllAds() {
        List<Ad> ads = adRepository.findAll();
        return ads.stream()
                .map(adMapper::toDTO) // Используем маппер для преобразования
                .collect(Collectors.toList());
    }
    @Override
    public List<AdDTO> getAdsMe(Integer userId) {
        List<Ad> userAds = adRepository.findByUserId(userId); // Убедитесь, что метод в репозитории правильно назван
        return userAds.stream()
                .map(adMapper::toDTO)
                .collect(Collectors.toList());
    }


}



