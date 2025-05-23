package ru.skypro.homework.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.skypro.homework.model.Image;
import ru.skypro.homework.service.impl.ImageServiceImpl;

import java.util.List;

@RestController
@RequestMapping("/api/images")
public class ImageController {

    @Autowired
    private ImageServiceImpl imageService;

    // Получить все изображения или изображение по ID
    @GetMapping
    public ResponseEntity<?> getImages(@RequestParam(required = false) Integer id) {
        if (id != null) {
            return imageService.getImageById(id)
                    .map(image -> new ResponseEntity<>(image, HttpStatus.OK))
                    .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
        } else {
            List<Image> images = imageService.getAllImages();
            return new ResponseEntity<>(images, HttpStatus.OK);
        }
    }

    // Создать новое изображение
    @PostMapping
    public ResponseEntity<Image> createImage(@RequestBody Image image) {
        Image createdImage = imageService.createImage(image);
        return new ResponseEntity<>(createdImage, HttpStatus.CREATED);
    }

    // Обновить существующее изображение
    @PutMapping("/{id}")
    public ResponseEntity<Image> updateImage(@PathVariable Integer id, @RequestBody Image imageDetails) {
        Image updatedImage = imageService.updateImage(id, imageDetails);
        return new ResponseEntity<>(updatedImage, HttpStatus.OK);
    }

    // Удалить изображение
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteImage(@PathVariable Integer id) {
        imageService.deleteImage(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
