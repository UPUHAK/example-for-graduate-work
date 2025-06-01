package ru.skypro.homework.exception;

public class ErrorUpdatingUsersException extends RuntimeException {
    public ErrorUpdatingUsersException(String message) {
        super(message);
    }

    public ErrorUpdatingUsersException(String message, Throwable cause) {
        super(message, cause);
    }
}

