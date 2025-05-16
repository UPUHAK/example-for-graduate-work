package ru.skypro.homework.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.*;

@Data
public class CreateOrUpdateAdDTO {

    @Schema(description = "заголовок объявления")
    @NotBlank
    @Size(min = 4, max = 32)
    private String title;

    @Schema(description = "цена объявления")
    @NotNull
    @Min(0)
    @Max(10000000)
    private Integer price;

    @Schema(description = "описание объявления")
    @NotBlank
    @Size(min = 8, max = 64)
    private String description;
}
