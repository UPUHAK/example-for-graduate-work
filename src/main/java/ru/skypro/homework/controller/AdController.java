package ru.skypro.homework.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import ru.skypro.homework.dto.Ad;
import ru.skypro.homework.dto.Ads;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@CrossOrigin(value = "http://localhost:3000")
@RestController
@RequestMapping("/ads")
@RequiredArgsConstructor
public class AdController {

    @GetMapping
    public ResponseEntity<List<Ad>> getAllAds() {

        return ResponseEntity.ok(List.of(new Ad()));
    }

    @PostMapping
    public ResponseEntity<Ad> addAd(@RequestBody Ad ad) {

        return ResponseEntity.status(201).body(ad);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Ad> getAd(@PathVariable Integer id) {

        return ResponseEntity.ok(new Ad());
    }
    @PatchMapping("/{id}")
    public ResponseEntity<Ad> updateAd(@PathVariable Integer id, @RequestBody Ad ad) {

        return ResponseEntity.ok(ad);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAd(@PathVariable Integer id) {

        return ResponseEntity.noContent().build();
    }



}