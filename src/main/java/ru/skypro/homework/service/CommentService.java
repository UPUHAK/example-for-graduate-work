package ru.skypro.homework.service;

import ru.skypro.homework.dto.CommentDTO;

import java.util.List;

public interface CommentService {

    List<CommentDTO> getCommentsByAdPk(Integer adPk);

    CommentDTO updateComment(Integer pk, CommentDTO commentDTO);
    void deleteComment(Integer pk);

    List<CommentDTO> getCommentsByAd(Integer pk);

    CommentDTO addComment(Integer adPk, CommentDTO commentDTO);
}
