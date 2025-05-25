package ru.skypro.homework.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class ImageDTO {

    @Schema(description = "Уникальный идентификатор изображения")
    private Integer id_image;

    @Schema(description = "URL изображения")
    private String imageUrl;

    @Schema(description = "Данные изображения в бинарном формате")
    private byte[] data;

    @Schema(description = "Идентификатор объявления")
    private Integer adId;

    @Schema(description = "Идентификатор пользователя")
    private Integer userId;
}
