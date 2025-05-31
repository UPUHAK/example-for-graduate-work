package ru.skypro.homework.service;

import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.model.Image;

import javax.persistence.EntityNotFoundException;
import java.io.IOException;

public interface AvatarService {
    Image updateAvatar(Integer userId, MultipartFile file) throws IOException, EntityNotFoundException;
}


