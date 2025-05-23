package ru.skypro.homework.service;

import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.model.Image;
import ru.skypro.homework.model.User;

import java.util.List;
import java.util.Optional;

public interface ImageService {

    Image saveAvatar(User user, MultipartFile file);

    Optional<Image> getImageById(Integer id);

    List<Image> getAllImages();

    Image createImage(Image image);

    Image updateImage(Integer id, Image imageDetails);

    void deleteImage(Integer id);
}

