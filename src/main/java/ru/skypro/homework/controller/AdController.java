package ru.skypro.homework.controller;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.dto.AdDTO;
import ru.skypro.homework.exception.AdNotFoundException;
import ru.skypro.homework.exception.UserNotFoundException;
import ru.skypro.homework.model.Ad;
import ru.skypro.homework.model.User;
import ru.skypro.homework.repository.AdRepository;
import ru.skypro.homework.repository.UserRepository;

import javax.validation.Valid;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import ru.skypro.homework.service.AdService;

// остальные импорты

@Slf4j
@CrossOrigin(value = "http://localhost:3000")
@RestController
@RequestMapping("/ads")
@RequiredArgsConstructor
public class AdController {

    private final AdRepository adRepository;
    private final UserRepository userRepository;
    private final AdService adService;

    @Operation(summary = "Добавление нового объявления")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Объявление успешно создано"),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден")
    })
    @PostMapping(consumes = {"multipart/form-data"})
    public ResponseEntity<AdDTO> addAd(
            @RequestPart("ad") @Valid AdDTO adDTO,
            @RequestPart(value = "image", required = false) MultipartFile imageFile) {
        log.info("Adding new ad: {}", adDTO);

        User user = userRepository.findById(adDTO.getAuthor())
                .orElseThrow(() -> new UserNotFoundException("User  not found"));

        Ad ad = new Ad();
        ad.setTitle(adDTO.getTitle());
        ad.setPrice(adDTO.getPrice());
        ad.setUser (user);

        if (imageFile != null && !imageFile.isEmpty()) {
            // Логика сохранения файла и установка URL изображения
            String imageUrl = saveImage(imageFile);
            ad.setImage(imageUrl); // Устанавливаем URL изображения в объект Ad
        } else {
            ad.setImage(adDTO.getImage()); // Если передаётся ссылка на изображение в JSON
        }

        Ad savedAd = adRepository.save(ad);
        AdDTO savedAdDTO = convertToDTO(savedAd);

        return ResponseEntity.status(HttpStatus.CREATED).body(savedAdDTO);
    }

    private String saveImage(MultipartFile imageFile) {
        return null;
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
                .orElseThrow(() -> new AdNotFoundException("Ad not found"));

        AdDTO adDTO = convertToDTO(ad);
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
                .orElseThrow(() -> new AdNotFoundException("Ad not found"));

        ad.setTitle(adDTO.getTitle());
        ad.setPrice(adDTO.getPrice());
        ad.setImage(adDTO.getImage());

        Ad updatedAd = adRepository.save(ad);
        AdDTO updatedAdDTO = convertToDTO(updatedAd);

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

    private AdDTO convertToDTO(Ad ad) {
        AdDTO adDTO = new AdDTO();
        adDTO.setPk(ad.getPk());
        adDTO.setTitle(ad.getTitle());
        adDTO.setPrice(ad.getPrice());
        adDTO.setImage(ad.getImage());
        adDTO.setAuthor(ad.getUser().getId());
        return adDTO;
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


}
