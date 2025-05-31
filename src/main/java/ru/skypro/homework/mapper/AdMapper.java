package ru.skypro.homework.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import ru.skypro.homework.dto.AdDTO;
import ru.skypro.homework.model.Ad;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import ru.skypro.homework.dto.AdDTO;
import ru.skypro.homework.model.Ad;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AdMapper {

    AdMapper INSTANCE = Mappers.getMapper(AdMapper.class);

    AdDTO toDTO(Ad ad);

    Ad toEntity(AdDTO dto);

    List<AdDTO> toDTOList(List<Ad> ads);

    List<Ad> toEntityList(List<AdDTO> dtos);
}

