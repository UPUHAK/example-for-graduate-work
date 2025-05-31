package ru.skypro.homework.service;

import ru.skypro.homework.dto.CommentDTO;

import java.util.List;

public interface CommentService {

    CommentDTO addComment(CommentDTO commentDTO);

    List<CommentDTO> getCommentsByAdPk(Integer adPk);

    CommentDTO updateComment(Integer pk, CommentDTO commentDTO);
    void deleteComment(Integer pk);

    List<CommentDTO> getCommentsByAd(Integer pk);
}
