package ru.skypro.homework.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NonNull;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;

@Data
public class Ad {

    @Schema(description = "id автора объявления")
    @NonNull
    @Positive
    private Integer author;

    @Schema(description = "ссылка на картинку объявления")
    private String image;

    @Schema(description = "id объявления")
    private Integer pk;

    @Schema(description = "цена объявления")
    @NonNull
    @Positive
    private Integer price;

    @Schema(description = "заголовок объявления")
    @NotBlank
    private String title;
}
