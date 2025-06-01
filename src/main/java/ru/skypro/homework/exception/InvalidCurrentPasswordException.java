package ru.skypro.homework.exception;

public class InvalidCurrentPasswordException extends RuntimeException {
    public InvalidCurrentPasswordException() {
        super();
    }

    public InvalidCurrentPasswordException(String message) {
        super(message);
    }

    public InvalidCurrentPasswordException(String message, Throwable cause) {
        super(message, cause);
    }
}
