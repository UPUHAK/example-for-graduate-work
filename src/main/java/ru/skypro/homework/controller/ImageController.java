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


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RestController
@CrossOrigin(value = "http://localhost:3000")
@RequestMapping("/api/images")
public class ImageController {

    @Autowired
    private ImageService imageService;

    @Operation(summary = "Получение изображения по ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Изображение успешно найдено"),
            @ApiResponse(responseCode = "404", description = "Изображение не найдено")
    })
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

    @Operation(summary = "Загрузка изображения для пользователя")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Изображение успешно загружено"),
            @ApiResponse(responseCode = "400", description = "Некорректный запрос"),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден"),
            @ApiResponse(responseCode = "500", description = "Ошибка сервера")
    })
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




