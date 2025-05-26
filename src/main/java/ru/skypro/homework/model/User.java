package ru.skypro.homework.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import ru.skypro.homework.dto.Role;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
@Schema(description = "Сущность пользователя, представляющая пользователя в системе")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false)
    @Schema(description = "Уникальный идентификатор пользователя", example = "1")
    private Integer id;

    @Email(message = "Email должен быть действительным")
    @NotBlank(message = "Email обязателен")
    @Column(name = "email", nullable = false, unique = true)
    @Schema(description = "Email адрес пользователя", example = "user@example.com")
    private String email;

    @NotBlank(message = "Имя обязательно")
    @Size(min = 2, max = 16)
    @Column(name = "first_name", nullable = false)
    @Schema(description = "Имя пользователя", example = "Иван")
    private String firstName;

    @NotBlank(message = "Фамилия обязательна")
    @Size(min = 2, max = 16)
    @Column(name = "last_name", nullable = false)
    @Schema(description = "Фамилия пользователя", example = "Иванов")
    private String lastName;

    @Pattern(regexp = "^\\+7\\s?\\(?\\d{3}\\)?\\s?\\d{3}\\s?-?\\d{2}\\s?-?\\d{2}$",
            message = "Телефон должен быть в формате +7 (XXX) XXX-XX-XX")
    @Column(name = "phone")
    @Schema(description = "Телефонный номер пользователя", example = "+71234567890")
    private String phone;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    @Schema(description = "Роль пользователя в системе", example = "ADMIN")
    private Role role;

    @Column(name = "image")
    @Schema(description = "URL профиля изображения пользователя", example = "http://example.com/image.jpg")
    private String image;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    @Schema(description = "Список комментариев пользователя")
    private List<Comment> comments;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    @Schema(description = "Список объявлений пользователя")
    private List<Ad> ads;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "image_id")
    @Schema(description = "Аватар пользователя")
    private Image avatar;

    @NotBlank(message = "Пароль обязателен")
    @Size(min = 6, message = "Пароль должен содержать минимум 6 символов")
    @Column(name = "password", nullable = false)
    @Schema(description = "Пароль пользователя")
    private String password;

    @NotBlank(message = "Имя пользователя обязательно")
    @Size(min = 3, max = 16)
    @Column(name = "username", nullable = false, unique = true)
    @Schema(description = "Имя пользователя для входа в систему", example = "ivan123")
    private String username;

    @Column(name = "is_authorize", nullable = false)
    @Schema(description = "Флаг, указывающий, авторизован ли пользователь", example = "true")
    private boolean isAuthorize = false;


}


