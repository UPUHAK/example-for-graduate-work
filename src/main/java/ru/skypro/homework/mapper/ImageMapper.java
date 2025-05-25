package ru.skypro.homework.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import ru.skypro.homework.dto.ImageDTO;
import ru.skypro.homework.model.Image;

@Mapper(componentModel = "spring")
public interface ImageMapper {


    @Mapping(source = "ad.pk", target = "adId")
    @Mapping(source = "user.id", target = "userId")
    ImageDTO imageToImageDTO(Image image);

    @Mapping(source = "adId", target = "ad.pk")
    @Mapping(source = "userId", target = "user.id")
    Image imageDTOToImage(ImageDTO imageDTO);
}