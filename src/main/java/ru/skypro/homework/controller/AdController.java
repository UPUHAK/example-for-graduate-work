package ru.skypro.homework.controller;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.skypro.homework.dto.AdDTO;
import ru.skypro.homework.model.Ad;
import ru.skypro.homework.model.User;
import ru.skypro.homework.repository.AdRepository;
import ru.skypro.homework.repository.UserRepository;

import java.util.Arrays;
import java.util.List;


@Slf4j
@CrossOrigin(value = "http://localhost:3000")
@RestController
@RequestMapping("/ads")
@RequiredArgsConstructor
public class AdController {

    @Autowired
    private AdRepository adRepository; // Репозиторий для работы с объявлениями

    @Autowired
    private UserRepository userRepository;

    @PostMapping
    public ResponseEntity<AdDTO> addAd(@RequestBody AdDTO adDTO) {
        // Получаем пользователя по id
        User user = userRepository.findById(adDTO.getAuthor())
                .orElseThrow(() -> new RuntimeException("User  not found"));

        // Создаем новое объявление
        Ad ad = new Ad();
        ad.setTitle(adDTO.getTitle());
        ad.setPrice(adDTO.getPrice());
        ad.setImage(adDTO.getImage());
        ad.setUser (user); // Устанавливаем пользователя

        // Сохраняем объявление в базе данных
        Ad savedAd = adRepository.save(ad);

        // Преобразуем сохраненное объявление в DTO
        AdDTO savedAdDTO = new AdDTO();
        savedAdDTO.setPk(savedAd.getPk());
        savedAdDTO.setTitle(savedAd.getTitle());
        savedAdDTO.setPrice(savedAd.getPrice());
        savedAdDTO.setImage(savedAd.getImage());
        savedAdDTO.setAuthor(savedAd.getUser ().getId()); // Здесь должно работать

        return ResponseEntity.status(201).body(savedAdDTO);
    }


    @GetMapping("/{id}")
    public ResponseEntity<AdDTO> getAd(@PathVariable Integer id) {

        return ResponseEntity.ok(new AdDTO());
    }

    @PatchMapping("/{id}")
    public ResponseEntity<AdDTO> updateAd(@PathVariable Integer id, @RequestBody AdDTO adDTO) {

        return ResponseEntity.ok(adDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAd(@PathVariable Integer id) {

        return ResponseEntity.noContent().build();
    }
}


