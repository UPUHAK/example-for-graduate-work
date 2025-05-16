package ru.skypro.homework.controller;



import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.skypro.homework.dto.AdDTO;

import java.util.List;



@Slf4j
@CrossOrigin(value = "http://localhost:3000")
@RestController
@RequestMapping("/ads")
@RequiredArgsConstructor
public class AdController {

    @GetMapping
    public ResponseEntity<List<AdDTO>> getAllAds() {

        return ResponseEntity.ok(List.of(new AdDTO()));
    }

    @PostMapping
    public ResponseEntity<AdDTO> addAd(@RequestBody AdDTO adDTO) {

        return ResponseEntity.status(201).body(adDTO);
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