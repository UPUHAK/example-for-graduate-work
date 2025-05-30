package ru.skypro.homework.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public  class UserDTO {

    @Schema(description = "id пользователя")
    private Integer id;

    @Schema(description = "Логин пользователя", example = "ivan@example.com")
    @Email(message = "Email должен быть корректным адресом электронной почты")
    private String email;

    @Schema(description = "пароль пользователя")
    @NotBlank(message = "Password is required") // Валидация для пароля
    private String password;


    @Schema(description = "имя пользователя")
    private String firstName;

    @Schema(description = "фамилия пользователя")
    private String lastName;

    @Schema(description = "телефон пользователя")
    private String phone;

    @Schema(description = "роль пользователя")
    private Role role;

    @Schema(description = "ссылка на аватар пользователя")
    private String avatar;

    @Schema(description = "Комментарии пользователя")
    private List<CommentDTO> comments;
}
