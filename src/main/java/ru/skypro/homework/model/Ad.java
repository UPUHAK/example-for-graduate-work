package ru.skypro.homework.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NonNull;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import java.util.List;

@Data
@Entity
public class Ad {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "id объявления")
    private Integer pk;

    @Schema(description = "ссылка на картинку объявления")
    private String image;

    @Schema(description = "цена объявления")
    @NonNull
    @Positive
    private Integer price;

    @Schema(description = "заголовок объявления")
    @NotBlank
    private String title;

    @Schema(description = "общее количество объявлений")
    private Integer count;

    @ManyToOne
    @JoinColumn(name = "author_id", nullable = false)
    @Schema(description = "Автор объявления")
    private User user; // Связь с пользователем

    @OneToMany(mappedBy = "ad", cascade = CascadeType.ALL)
    @Schema(description = "Список комментариев к объявлению")
    private List<Comment> comments;


}
