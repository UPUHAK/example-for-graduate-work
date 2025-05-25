package ru.skypro.homework.model;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "image")
public class Image {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_image", nullable = false, updatable = false)
    @Schema(description = "Уникальный идентификатор изображения")
    private Integer id;

    @Column(name = "image_url", nullable = false)
    @Schema(description = "URL изображения")
    private String imageUrl;

    @Lob
    @Schema(description = "Данные изображения в бинарном формате")
    private byte[] data;

    @ManyToOne
    @JoinColumn(name = "ad_id")
    private Ad ad;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}
