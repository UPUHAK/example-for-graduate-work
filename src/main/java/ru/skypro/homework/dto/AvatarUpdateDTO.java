package ru.skypro.homework.dto;

import org.springframework.web.multipart.MultipartFile;

public class AvatarUpdateDTO {

    private MultipartFile file;

    public MultipartFile getFile() {
        return file;
    }

    public void setFile(MultipartFile file) {
        this.file = file;
    }
}

