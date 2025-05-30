package ru.skypro.homework.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.skypro.homework.repository.CommentRepository;

@Component("commentSecurity")
public class CommentSecurity {

    @Autowired
    private CommentRepository commentRepository;

    public boolean isCommentOwner(Integer commentId, String username) {
        return commentRepository.findById(commentId)
                .map(comment -> comment.getUser().getEmail().equals(username))
                .orElse(false);
    }
}
