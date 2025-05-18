package ru.skypro.homework.test;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.skypro.homework.dto.CommentDTO;
import ru.skypro.homework.mapper.CommentMapper;
import ru.skypro.homework.model.Comment;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class CommentMapperTest {

    private final CommentMapper commentMapper = Mappers.getMapper(CommentMapper.class);

    @Test
    public void testToDTO() {
        /* Создаем тестовый объект Comment

         */
        Comment comment = new Comment();
        comment.setPk(1);
        comment.setText("Тестовый комментарий");

        /*
         Преобразуем в CommentDTO
         */
        CommentDTO commentDTO = commentMapper.toDTO(comment);

        /*
         Проверяем, что преобразование прошло успешно
         */
        assertEquals(comment.getPk(), commentDTO.getPk());
        assertEquals(comment.getText(), commentDTO.getText());
    }

    @Test
    public void testToEntity() {
        /*
         Создаем тестовый объект CommentDTO
         */
        CommentDTO commentDTO = new CommentDTO();
        commentDTO.setPk(1);
        commentDTO.setText("Тестовый комментарий");

        /*
         Преобразуем в Comment
         */
        Comment comment = commentMapper.toEntity(commentDTO);

        /*
         Проверяем, что преобразование прошло успешно
         */
        assertEquals(commentDTO.getPk(), comment.getPk());
        assertEquals(commentDTO.getText(), comment.getText());
    }

    @Test
    public void testToDTOList() {
        /*
         Создаем список Comment
         */
        Comment comment1 = new Comment();
        comment1.setPk(1);
        comment1.setText("Комментарий 1");

        Comment comment2 = new Comment();
        comment2.setPk(2);
        comment2.setText("Комментарий 2");

        List<Comment> comments = Arrays.asList(comment1, comment2);

        /*
         Преобразуем список Comment в список CommentDTO
         */
        List<CommentDTO> commentDTOs = commentMapper.toDTOList(comments);

        /*
         Проверяем, что преобразование прошло успешно
         */
        assertEquals(comments.size(), commentDTOs.size());
        assertEquals(comments.get(0).getPk(), commentDTOs.get(0).getPk());
        assertEquals(comments.get(1).getText(), commentDTOs.get(1).getText());
    }

    @Test
    public void testToEntityList() {
        /*
         Создаем список CommentDTO
         */
        CommentDTO commentDTO1 = new CommentDTO();
        commentDTO1.setPk(1);
        commentDTO1.setText("Комментарий DTO 1");

        CommentDTO commentDTO2 = new CommentDTO();
        commentDTO2.setPk(2);
        commentDTO2.setText("Комментарий DTO 2");

        List<CommentDTO> commentDTOs = Arrays.asList(commentDTO1, commentDTO2);

        /*
         Преобразуем список CommentDTO в список Comment
         */
        List<Comment> comments = commentMapper.toEntityList(commentDTOs);

        /*
         Проверяем, что преобразование прошло успешно
         */
        assertEquals(commentDTOs.size(), comments.size());
        assertEquals(commentDTOs.get(0).getPk(), comments.get(0).getPk());
        assertEquals(commentDTOs.get(1).getText(), comments.get(1).getText());
    }
}

