package ru.skypro.homework.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
@NoArgsConstructor
public  class UserDTO {

    @Schema(description = "id пользователя")
    private Integer id;

    @Schema(description = "логин пользователя")
    @NotBlank(message = "Username is required")
    private String username;

    @Schema(description = "пароль пользователя")
    @NotBlank(message = "Password is required") // Валидация для пароля
    private String password;

    @Schema(description = "логин пользователя")
    @Email
    private String email;

    @Schema(description = "имя пользователя")
    private String firstName;

    @Schema(description = "фамилия пользователя")
    private String lastName;

    @Schema(description = "телефон пользователя")
    private String phone;

    @Schema(description = "роль пользователя")
    private Role role;

    @Schema(description = "ссылка на аватар пользователя")
    private String image;
}
