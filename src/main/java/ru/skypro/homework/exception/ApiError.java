package ru.skypro.homework.exception;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
public class ApiError {
    private int status;
    private String message;
    private long timestamp;

    public ApiError(int status, String message) {
        this.status = status;
        this.message = message;
        this.timestamp = System.currentTimeMillis();
    }
}