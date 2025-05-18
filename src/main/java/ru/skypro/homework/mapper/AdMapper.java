package ru.skypro.homework.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import ru.skypro.homework.dto.AdDTO;
import ru.skypro.homework.model.Ad;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AdMapper {

    AdMapper INSTANCE = Mappers.getMapper(AdMapper.class);

    /*
     Преобразование Ad в AdDTO
     */
    AdDTO toDTO(Ad ad);

    /*
     Преобразование AdDTO в Ad
     */
    Ad toEntity(AdDTO dto);

    /*
     Преобразование списка Ad в список AdDTO
     */
    List<AdDTO> toDTOList(List<Ad> ads);

    /*
     Преобразование списка AdDTO в список Ad
     */
    List<Ad> toEntityList(List<AdDTO> dtos);
}
