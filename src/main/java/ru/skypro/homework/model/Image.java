package ru.skypro.homework.model;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "image")
public class Image {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_image", nullable = false, updatable = false)
    @Schema(description = "Уникальный идентификатор изображения")
    private Integer id_image;

    @Column(name = "image_url", nullable = false)
    @Schema(description = "URL изображения")
    private String imageUrl;

    @Column(name = "image_type")
    @Schema(description = "Тип файла изображения (например, JPEG, PNG)")
    private String imageType;

    @ManyToOne
    @JoinColumn(name = "ad_id")
    private Ad ad;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}
