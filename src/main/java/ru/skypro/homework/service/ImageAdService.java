package ru.skypro.homework.service;

import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.model.Image;

import javax.persistence.EntityNotFoundException;
import java.io.IOException;

public interface ImageAdService {
    Image saveAdImage(Integer adId, MultipartFile file) throws IOException, EntityNotFoundException;
}
