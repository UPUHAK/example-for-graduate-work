package ru.skypro.homework.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.skypro.homework.model.Image;
import ru.skypro.homework.service.ImageService;


@CrossOrigin(value = "http://localhost:3000")
@RestController
@RequestMapping("/api/images")
public class ImageController {

    @Autowired
    private ImageService imageService;


    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @imageService.isOwner(#id, authentication.name)")
    public ResponseEntity<Image> getImageById(@PathVariable Integer id) {
        if (imageService == null) {
            throw new IllegalStateException("ImageService is not initialized");
        }

        return imageService.getImageById(id)
                .map(image -> ResponseEntity.ok(image))
                .orElse(ResponseEntity.notFound().build());
    }


}
