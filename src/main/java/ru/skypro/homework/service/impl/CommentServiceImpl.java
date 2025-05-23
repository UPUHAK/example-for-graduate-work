package ru.skypro.homework.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.skypro.homework.dto.CommentDTO;
import ru.skypro.homework.exception.CommentNotFoundException;
import ru.skypro.homework.exception.UserNotFoundException;
import ru.skypro.homework.mapper.CommentMapperImpl;
import ru.skypro.homework.model.Comment;
import ru.skypro.homework.model.User;
import ru.skypro.homework.model.Ad;
import ru.skypro.homework.repository.CommentRepository;
import ru.skypro.homework.repository.UserRepository;
import ru.skypro.homework.repository.AdRepository;
import ru.skypro.homework.service.CommentService;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final AdRepository adRepository;
    private final CommentMapperImpl commentMapper;

    @Override
    @Transactional
    public CommentDTO addComment(CommentDTO commentDTO) {
        User user = userRepository.findById(commentDTO.getAuthor())
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        Ad ad = adRepository.findById(commentDTO.getPk())
                .orElseThrow(() -> new RuntimeException("Ad not found"));

        Comment comment = new Comment();
        comment.setText(commentDTO.getText());
        comment.setUser(user);
        comment.setAd(ad);

        Comment savedComment = commentRepository.save(comment);
        return commentMapper.toDTO(savedComment);
    }

    @PreAuthorize("hasRole('ADMIN') or @commentSecurity.isCommentOwner(#id, authentication.name)")
    @Override
    @Transactional
    public CommentDTO updateComment(Integer id, CommentDTO commentDTO) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new CommentNotFoundException("Comment not found"));

        comment.setText(commentDTO.getText());

        Comment updatedComment = commentRepository.save(comment);
        return commentMapper.toDTO(updatedComment);
    }

    @PreAuthorize("hasRole('ADMIN') or @commentSecurity.isCommentOwner(#id, authentication.name)")
    @Override
    @Transactional
    public void deleteComment(Integer id) {
        if (!commentRepository.existsById(id)) {
            throw new CommentNotFoundException("Comment not found");
        }
        commentRepository.deleteById(id);
    }
}