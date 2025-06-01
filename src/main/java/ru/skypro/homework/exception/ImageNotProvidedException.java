package ru.skypro.homework.exception;


public class ImageNotProvidedException extends RuntimeException {
    public ImageNotProvidedException(String message) {
        super(message);
    }

    public ImageNotProvidedException(String message, Throwable cause) {
        super(message, cause);
    }
}
