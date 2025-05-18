package ru.skypro.homework.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.skypro.homework.dto.CommentDTO;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "comment")
public class Comment {


    @Column(name = "author_id", nullable = false, updatable = false)
    @Schema(description = "id автора комментария")
    private Integer author;

    @Column(name = "image")
    @Schema(description = "ссылка на аватар автора комментария", example = "http://example.com/image.jpg")
    private String authorImage;

    @NotBlank(message = "Имя обязательно")
    @Size(min = 2, max = 16)
    @Column(name = "first_name", nullable = false)
    @Schema(description = "имя создателя комментария")
    private String authorFirstName;

    @NotBlank(message = "Дата создания комментария обязательна")
    @Column(name = "created_at")
    @Schema(description = "дата и время создания комментария в миллисекундах с 00:00:00 01.01.1970")
    private Long createdAt;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pk", nullable = false, updatable = false)
    @Schema(description = "pk комментария")
    private Integer pk;

    @Column(name = "text", nullable = false)
    @Schema(description = "текст комментария")
    private String text;

    @Column(name = "count", nullable = false)
    @Schema(description = "Общее количество комментариев")
    private Integer count = 0;

    @Column(name = "results")
    @Schema(description = "список комментариев")
    private List<CommentDTO> results;
}

