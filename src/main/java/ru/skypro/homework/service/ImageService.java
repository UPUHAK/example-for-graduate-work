package ru.skypro.homework.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.skypro.homework.exception.ResourceNotFoundException;
import ru.skypro.homework.model.Image;
import ru.skypro.homework.repository.ImageRepository;

import java.util.List;
import java.util.Optional;

@Service
public class ImageService {

    @Autowired
    private ImageRepository imageRepository;


    public List<Image> getAllImages() {
        return imageRepository.findAll();
    }


    public Optional<Image> getImageById(Integer id) {
        return imageRepository.findById(id);
    }


    public Image createImage(Image image) {
        return imageRepository.save(image);
    }


    public Image updateImage(Integer id, Image imageDetails) {
        /*
         Проверяем, существует ли изображение
         */
        if (imageRepository.existsById(id)) {
            imageDetails.setId_image(id);
            return imageRepository.save(imageDetails);
        } else {
            throw new ResourceNotFoundException("Image not found with id " + id);
        }
    }


    public void deleteImage(Integer id) {
        if (imageRepository.existsById(id)) {
            imageRepository.deleteById(id);
        } else {
            throw new ResourceNotFoundException("Image not found with id " + id);
        }
    }
}
