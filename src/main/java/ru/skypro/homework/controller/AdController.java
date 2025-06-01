package ru.skypro.homework.controller;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.dto.AdDTO;
import ru.skypro.homework.dto.ImageDTO;
import ru.skypro.homework.exception.AdNotFoundException;
import ru.skypro.homework.exception.ImageNotProvidedException;
import ru.skypro.homework.exception.UserNotFoundException;
import ru.skypro.homework.mapper.AdMapper;
import ru.skypro.homework.mapper.ImageMapper;
import ru.skypro.homework.model.Ad;
import ru.skypro.homework.model.Image;
import ru.skypro.homework.model.User;
import ru.skypro.homework.repository.AdRepository;
import ru.skypro.homework.repository.UserRepository;

import javax.persistence.EntityNotFoundException;
import javax.validation.Valid;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import ru.skypro.homework.service.AdService;
import ru.skypro.homework.service.ImageAdService;
import ru.skypro.homework.service.ImageStorageService;


@Slf4j
@CrossOrigin(value = "http://localhost:3000")
@RestController
@RequestMapping("/ads")
@RequiredArgsConstructor
public class AdController {

    private final AdRepository adRepository;
    private final UserRepository userRepository;
    private final AdService adService;
    private final AdMapper adMapper;
    private final ImageAdService imageAdService;
    private final ImageMapper imageMapper;

    @Autowired
    private final ImageStorageService imageStorageService;

    @Operation(summary = "Добавление нового объявления")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Объявление успешно создано"),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден")
    })
    @PostMapping(consumes = {"multipart/form-data"})
    public ResponseEntity<AdDTO> addAd(
            @RequestPart("ad") @Valid AdDTO adDTO,
            @RequestPart(value = "image", required = false) MultipartFile imageFile) throws IOException {

        log.info("Adding new ad: {}", adDTO);

        User user = userRepository.findById(adDTO.getAuthor())
                .orElseThrow(() -> new UserNotFoundException("Пользователь не найден"));

        Ad ad = adMapper.toEntity(adDTO);
        ad.setUser (user);

        if (imageFile != null && !imageFile.isEmpty()) {
            String imageUrl = imageStorageService.store(imageFile);
            ad.setImage(imageUrl);
        } else {
            ad.setImage(adDTO.getImage());
        }

        Ad savedAd = adRepository.save(ad);
        AdDTO savedAdDTO = adMapper.toDTO(savedAd);

        return ResponseEntity.status(HttpStatus.CREATED).body(savedAdDTO);
    }



    @Operation(summary = "Получение объявления по ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Объявление найдено"),
            @ApiResponse(responseCode = "404", description = "Объявление не найдено")
    })
    @GetMapping("/{id}")
    public ResponseEntity<AdDTO> getAd(@PathVariable Integer id) {
        log.info("Fetching ad with id: {}", id);

        Ad ad = adRepository.findById(id)
                .orElseThrow(() -> new AdNotFoundException("Объявление не найдено"));

        // Используем маппер для преобразования сущности Ad в AdDTO
        AdDTO adDTO = AdMapper.INSTANCE.toDTO(ad);
        return ResponseEntity.ok(adDTO);
    }


    @Operation(summary = "Обновление объявления по ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Объявление успешно обновлено"),
            @ApiResponse(responseCode = "404", description = "Объявление не найдено")
    })
    @PutMapping("/{id}")
    public ResponseEntity<AdDTO> updateAd(@PathVariable Integer id, @Valid @RequestBody AdDTO adDTO) {
        log.info("Updating ad with id: {}", id);

        Ad ad = adRepository.findById(id)
                .orElseThrow(() -> new AdNotFoundException("Объявление не найдено"));

        // Используем маппер для обновления полей сущности Ad
        Ad updatedAd = AdMapper.INSTANCE.toEntity(adDTO);
        updatedAd.setPk(ad.getPk()); // Сохраняем ID, чтобы обновить существующее объявление

        // Сохраняем обновленное объявление в базе данных
        Ad savedAd = adRepository.save(updatedAd);

        // Преобразуем обратно в DTO
        AdDTO updatedAdDTO = AdMapper.INSTANCE.toDTO(savedAd);

        return ResponseEntity.ok(updatedAdDTO);
    }

    @Operation(summary = "Удаление объявления по ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Объявление успешно удалено"),
            @ApiResponse(responseCode = "404", description = "Объявление не найдено")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAd(@PathVariable Integer id) {
        log.info("Deleting ad with id: {}", id);

        if (!adRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        adRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }



    @Operation(summary = "Получение списка всех объявлений")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Список объявлений успешно получен")
    })
    @GetMapping
    public ResponseEntity<List<AdDTO>> getAllAds() {
        log.info("Fetching all ads");
        List<AdDTO> adDTOs = adService.getAllAds(); // Вызываем метод сервиса
        return ResponseEntity.ok(adDTOs);
    }
    @Operation(summary = "Получение списка объявлений авторизованного пользователя")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Список объявлений успешно получен"),
            @ApiResponse(responseCode = "204", description = "У пользователя нет объявлений")
    })
    @GetMapping("/my-ads")
    public ResponseEntity<List<AdDTO>> getAdsMe(@RequestParam Integer userId) {
        log.info("Fetching ads for user with id: {}", userId);

        List<Ad> userAds = adRepository.findByUserId(userId);
        if (userAds.isEmpty()) {
            return ResponseEntity.noContent().build(); // 204 No Content
        }

        // Используем маппер для преобразования
        List<AdDTO> adDTOs = adMapper.toDTOs(userAds);

        return ResponseEntity.ok(adDTOs);
    }

    @Operation(summary = "Обновление изображения объявления по ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Изображение успешно обновлено"),
            @ApiResponse(responseCode = "404", description = "Объявление не найдено")
    })
    public String updateAdImage(Integer id, MultipartFile imageFile) throws IOException {
        // Поиск объявления по идентификатору
        Ad ad = adRepository.findById(id)
                .orElseThrow(() -> new AdNotFoundException("Объявление не найдено"));

        // Проверка наличия файла изображения
        if (imageFile == null || imageFile.isEmpty()) {
            throw new ImageNotProvidedException("Необходимо предоставить изображение для обновления");
        }

        // Проверка типа файла (JPEG/PNG)
        String contentType = imageFile.getContentType();
        if (contentType == null || (!contentType.equals("image/jpeg") && !contentType.equals("image/png"))) {
            throw new IllegalArgumentException("Только файлы JPEG и PNG поддерживаются");
        }

        // Сохранение файла и получение относительного пути к нему
        String imagePath = imageStorageService.store(imageFile);
        ad.setImage(imagePath); // Устанавливаем путь к изображению в объект Ad

        // Сохраняем обновленное объявление в базе данных
        adRepository.save(ad);

        return imagePath; // Возвращаем путь к обновленному изображению
    }


    @PostMapping("/{adId}/images")
    @Operation(summary = "Обновить изображение объявления",
            description = "Обновляет изображение для указанного объявления по его идентификатору.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Изображение успешно обновлено"),
            @ApiResponse(responseCode = "404", description = "Объявление не найдено"),
            @ApiResponse(responseCode = "500", description = "Ошибка сервера при обработке файла")
    })
    public ResponseEntity<ImageDTO> updateImage(@PathVariable Integer adId, @RequestParam("file") MultipartFile file) {
        try {
            Image image = imageAdService.saveAdImage(adId, file);
            ImageDTO imageDTO = imageMapper.imageToImageDTO(image); // Используем внедрённый маппер
            return ResponseEntity.status(HttpStatus.CREATED).body(imageDTO);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }


}
