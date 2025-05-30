package ru.skypro.homework.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;
import ru.skypro.homework.dto.UpdateUserDTO;
import ru.skypro.homework.dto.UserDTO;
import ru.skypro.homework.model.User;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {

    /*
     Преобразование User в UserDTO
     */
    UserDTO toDTO(User user);

    /*
     Преобразование UserDTO в User
     */
    User toEntity(UserDTO dto);

    /*
     Обновление User из UpdateUser DTO
     */
    void updateEntityFromDTO(UpdateUserDTO updateDTO, @MappingTarget User user);

    /*
     Преобразование списка User в список UserDTO
     */
    List<UserDTO> toDTOList(List<User> users);

    /*
     Преобразование списка UserDTO в список User
     */
    List<User> toEntityList(List<UserDTO> dtos);
}


