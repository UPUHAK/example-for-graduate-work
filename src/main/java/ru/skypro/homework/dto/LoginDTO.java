package ru.skypro.homework.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class LoginDTO {

    @Schema(description = "пароль")
    @NotBlank
    @Size(min = 8, max = 16)
    private String password;

    @Schema(description = "логин")
    @NotBlank
    @Size(min = 4, max = 32)
    private String username;

    public @NotBlank @Size(min = 8, max = 16) String getPassword() {
        return password;
    }

    public @NotBlank @Size(min = 4, max = 32) String getUsername() {
        return username;
    }
}
