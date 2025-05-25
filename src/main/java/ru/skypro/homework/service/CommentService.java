package ru.skypro.homework.service;

import ru.skypro.homework.dto.CommentDTO;

public interface CommentService {

    CommentDTO addComment(CommentDTO commentDTO);
    CommentDTO updateComment(Integer id, CommentDTO commentDTO);
    void deleteComment(Integer id);
}
