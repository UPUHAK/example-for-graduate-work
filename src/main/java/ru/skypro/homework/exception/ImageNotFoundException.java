package ru.skypro.homework.exception;

public class ImageNotFoundException extends RuntimeException {
    public ImageNotFoundException(Integer id) {
        super("Изображение с ID " + id + " не найдено");
    }
}
