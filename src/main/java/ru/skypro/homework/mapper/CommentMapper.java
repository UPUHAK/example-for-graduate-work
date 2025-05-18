package ru.skypro.homework.mapper;

import org.mapstruct.Mapper;
import ru.skypro.homework.dto.CommentDTO;
import ru.skypro.homework.model.Comment;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CommentMapper {

    /*
     Преобразование Comment в CommentDTO
     */
    CommentDTO toDTO(Comment comment);

    /*
     Преобразование CommentDTO в Comment
     */
    Comment toEntity(CommentDTO dto);

    /*
     Преобразование списка Comment в список CommentDTO
     */
    List<CommentDTO> toDTOList(List<Comment> comments);

    /*
     Преобразование списка CommentDTO в список Comment
     */
    List<Comment> toEntityList(List<CommentDTO> dtos);
}


