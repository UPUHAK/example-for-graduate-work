package ru.skypro.homework.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import ru.skypro.homework.dto.UpdateUserDTO;
import ru.skypro.homework.dto.UserDTO;
import ru.skypro.homework.model.Image;
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
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", ignore = true)
    void updateEntityFromDTO(UpdateUserDTO dto, @MappingTarget User entity);

    /*
     Преобразование списка User в список UserDTO
     */
    List<UserDTO> toDTOList(List<User> users);

    /*
     Преобразование списка UserDTO в список User
     */
    List<User> toEntityList(List<UserDTO> dtos);

    /*
     Преобразование Image в String (URL)
     */
    default String map(Image image) {
        return image != null ? image.getImageUrl() : null;
    }

    /*
     Преобразование String (URL) в Image
     */
    default Image map(String path) {
        if (path == null) {
            return null;
        }
        Image image = new Image();
        image.setImageUrl(path);
        return image;
    }
}



