package ru.skypro.homework.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.dto.ImageDTO;
import ru.skypro.homework.exception.ImageNotFoundException;
import ru.skypro.homework.mapper.ImageMapper;
import ru.skypro.homework.model.Image;
import ru.skypro.homework.model.User;
import ru.skypro.homework.repository.ImageRepository;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import ru.skypro.homework.repository.UserRepository;
import ru.skypro.homework.service.ImageService;
import javax.persistence.EntityNotFoundException;

@Service
public class ImageServiceImpl implements ImageService {

    private final ImageRepository imageRepository;
    private final ImageMapper imageMapper;
    private final UserRepository userRepository;

    private static final Logger log = LoggerFactory.getLogger(ImageServiceImpl.class);


    @Autowired
    public ImageServiceImpl(ImageRepository imageRepository, ImageMapper imageMapper, UserRepository userRepository) {
        this.imageRepository = imageRepository;
        this.imageMapper = imageMapper;
        this.userRepository = userRepository;
    }

    public ImageDTO convertToDto(Image image) {
        return imageMapper.imageToImageDTO(image);
    }

    public Image convertToEntity(ImageDTO imageDTO) {
        return imageMapper.imageDTOToImage(imageDTO);
    }
    @Override
    public Optional<Image> findImageById(Integer id) {
        return imageRepository.findById(id);
    }

    @Override
    public Image saveImage(Integer userId, MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Файл не должен быть пустым");
        }
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь с ID " + userId + " не найден"));

        Image image = new Image();
        image.setUser(user);
        image.setData(file.getBytes());

        return imageRepository.save(image);
    }


    @Override
    public Optional<Image> getImageById(Integer id) {
        return imageRepository.findById(id);
    }

    @Override
    public List<Image> getAllImages() {
        return imageRepository.findAll();
    }

    @Override
    public Image createImage(Image image) {
        return imageRepository.save(image);
    }

    @Override
    public Image updateImage(Integer id, Image imageDetails) {
        Optional<Image> existingImage = imageRepository.findById(id);
        if (existingImage.isPresent()) {
            Image imageToUpdate = existingImage.get();
            imageToUpdate.setImageUrl(imageDetails.getImageUrl());
            return imageRepository.save(imageToUpdate);
        }
        throw new ImageNotFoundException(id);
    }

    @Override
    public void deleteImage(Integer id) {
        if (!imageRepository.existsById(id)) {
            throw new EntityNotFoundException("Изображение с ID " + id + " не найдено.");
        }

        imageRepository.deleteById(id);
    }

    private Optional<Image> findImage(Integer userId) {
        return imageRepository.findByUserId(userId);
    }

    @Transactional
    public void saveToDatabase(ImageDTO imageDTO, Path imagePath, MultipartFile imageFile) {
        Optional<Image> optionalImage = imageRepository.findByUserId(imageDTO.getUserId());

        Image image = optionalImage.orElseGet(() -> {
            Image newImage = new Image();
            User user = userRepository.findById(imageDTO.getUserId())
                    .orElseThrow(() -> new EntityNotFoundException("Пользователь с ID " + imageDTO.getUserId() + " не найден"));
            newImage.setUser(user);
            return newImage;
        });


        image.setImageUrl(imagePath.toString());
        try {
            image.setData(imageFile.getBytes());
        } catch (IOException e) {
            log.error("Error reading file for user {}: {}", imageDTO.getUserId(), e.getMessage());
            throw new RuntimeException("Ошибка при чтении файла. Попробуйте снова.", e);
        }
        imageRepository.save(image);
    }


}






