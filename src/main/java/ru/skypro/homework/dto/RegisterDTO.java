package ru.skypro.homework.dto;

import javax.validation.constraints.*;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class RegisterDTO {

    @Schema(description = "логин (email)")
    @NotBlank(message = "Email обязателен")
    @Email(message = "Неверный формат email")
    @Size(min = 4, max = 32, message = "Длина email должна быть от 4 до 32 символов")
    private String username;

    @Schema(description = "пароль")
    @NotBlank(message = "Пароль обязателен")
    @Size(min = 8, max = 16, message = "Длина пароля должна быть от 8 до 16 символов")
    private String password;

    @Schema(description = "имя пользователя")
    @NotBlank(message = "Имя обязательно")
    @Size(min = 2, max = 16, message = "Длина имени должна быть от 2 до 16 символов")
    private String firstName;

    @Schema(description = "фамилия пользователя")
    @NotBlank(message = "Фамилия обязательна")
    @Size(min = 2, max = 16, message = "Длина фамилии должна быть от 2 до 16 символов")
    private String lastName;

    @Schema(description = "телефон пользователя")
    @Pattern(regexp = "\\+7\\s?\\(?\\d{3}\\)?\\s?\\d{3}-?\\d{2}-?\\d{2}", message = "Телефон должен соответствовать формату +7(___)___-__-__")
    private String phone;

    @Schema(description = "роль пользователя")
    @NotNull(message = "Роль пользователя обязательна")
    private Role role;


    @JsonCreator
    public RegisterDTO(

            @JsonProperty("password") String password,
            @JsonProperty("firstName") String firstName,
            @JsonProperty("lastName") String lastName,
            @JsonProperty("phone") String phone,
            @JsonProperty("role") Role role,
            @JsonProperty("username") String email) {

        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phone = phone;
        this.role = role;
        this.username = email;
    }
}
