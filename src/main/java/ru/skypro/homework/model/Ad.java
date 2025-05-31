package ru.skypro.homework.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "ads")
@ToString(exclude = {"user", "comments", "images"})
public class Ad {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false)
    @Schema(description = "Уникальный идентификатор объявления")
    private Integer pk;

    @Column(name = "image")
    @Schema(description = "Ссылка на картинку объявления")
    private String image;

    @Column(name = "price", nullable = false)
    @Schema(description = "Цена объявления")
    @Positive(message = "Цена должна быть положительной")
    private Integer price;

    @Column(name = "title", nullable = false)
    @Schema(description = "Заголовок объявления")
    @NotBlank(message = "Заголовок обязателен")
    private String title;

    @Column(name = "count")
    @Schema(description = "Общее количество объявлений")
    private Integer count;

    @ManyToOne
    @JoinColumn(name = "author_id", nullable = false)
    @Schema(description = "Автор объявления")
    private User user; // Связь с пользователем

    @OneToMany(mappedBy = "ad", cascade = CascadeType.ALL)
    @Schema(description = "Список комментариев к объявлению")
    private List<Comment> comments;

    @OneToMany(mappedBy = "ad", cascade = CascadeType.ALL)
    @Schema(description = "Список изображений, связанных с объявлением")
    private List<Image> images; // Список изображений, связанных с объявлением

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Ad)) return false;
        Ad ad = (Ad) o;
        return Objects.equals(pk, ad.pk);
    }

    @Override
    public int hashCode() {
        return Objects.hash(pk);
    }
}

