package ru.skypro.homework.dto;

public class ChangePasswordRequest {
    private String oldPassword;
    private String newPassword;

    // Конструктор
    public ChangePasswordRequest(String oldPassword, String newPassword) {
        this.oldPassword = oldPassword;
        this.newPassword = newPassword;
    }

    // Геттер для старого пароля
    public String getOldPassword() {
        return oldPassword;
    }

    // Геттер для нового пароля
    public String getNewPassword() {
        return newPassword;
    }

    // Сеттеры (если необходимо)
    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
}

