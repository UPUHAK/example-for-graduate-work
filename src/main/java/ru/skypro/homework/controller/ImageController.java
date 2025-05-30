package ru.skypro.homework.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.dto.ImageDTO;
import ru.skypro.homework.model.Image;
import ru.skypro.homework.service.ImageService;

import javax.persistence.EntityNotFoundException;
import java.io.IOException;

@CrossOrigin(value = "http://localhost:3000")
@RestController
@CrossOrigin(value = "http://localhost:3000")
@RequestMapping("/api/images")
public class ImageController {

    @Autowired
    private ImageService imageService;


    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<Image> getImageById(@PathVariable Integer id) {
        if (imageService == null) {
            throw new IllegalStateException("ImageService is not initialized");
        }

        return imageService.getImageById(id)
                .map(image -> ResponseEntity.ok(image))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{userId}")
    public ResponseEntity<ImageDTO> uploadImage(@PathVariable Integer userId, @RequestParam("file") MultipartFile file) {
        try {
            Image savedImage = imageService.saveImage(userId, file);
            ImageDTO imageDTO = imageService.convertToDto(savedImage);
            return ResponseEntity.status(HttpStatus.CREATED).body(imageDTO);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }
}



