package ru.skypro.homework.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import ru.skypro.homework.dto.CommentDTO;
import ru.skypro.homework.model.Comment;
import ru.skypro.homework.model.User;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CommentMapper {

    // Преобразование Comment в CommentDTO с маппингом user -> author и дополнительными полями
    @Mapping(target = "author", source = "user.id")
    @Mapping(target = "authorImage", source = "user.image", qualifiedByName = "mapUserAvatar")
    @Mapping(target = "authorFirstName", source = "user.firstName")
    CommentDTO toDTO(Comment comment);

    // Преобразование CommentDTO в Comment с маппингом author (id) -> user
    @Mapping(target = "user", source = "author", qualifiedByName = "mapAuthorIdToUser")
    Comment toEntity(CommentDTO dto);

    List<CommentDTO> toDTOList(List<Comment> comments);

    List<Comment> toEntityList(List<CommentDTO> dtos);

    // метод для преобразования id автора в User
    @Named("mapAuthorIdToUser")
    default User mapAuthorIdToUser(Integer authorId) {
        if (authorId == null) {
            return null;
        }
        User user = new User();
        user.setId(authorId);
        return user;
    }

    // метод для обработки аватара
    @Named("mapUserAvatar")
    default String mapUserAvatar(String image) {
        return image != null ? image : "";
    }
}


