package ru.skypro.homework.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.skypro.homework.dto.CommentDTO;
import ru.skypro.homework.exception.AdNotFoundException;
import ru.skypro.homework.exception.CommentNotFoundException;
import ru.skypro.homework.exception.UserNotFoundException;
import ru.skypro.homework.mapper.CommentMapper;
import ru.skypro.homework.model.Comment;
import ru.skypro.homework.model.User;
import ru.skypro.homework.model.Ad;
import ru.skypro.homework.repository.CommentRepository;
import ru.skypro.homework.repository.UserRepository;
import ru.skypro.homework.repository.AdRepository;
import ru.skypro.homework.service.CommentService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final AdRepository adRepository;
    private final CommentMapper commentMapper;

    @Override
    @Transactional
    public CommentDTO addComment(CommentDTO commentDTO) {
        User user = userRepository.findById(commentDTO.getAuthor())
                .orElseThrow(() -> new UserNotFoundException("User  not found"));
        Ad ad = adRepository.findById(commentDTO.getPk())
                .orElseThrow(() -> new AdNotFoundException("Ad not found"));

        Comment comment = new Comment();
        comment.setText(commentDTO.getText());
        comment.setUser (user);
        comment.setAd(ad);

        Comment savedComment = commentRepository.save(comment);
        return commentMapper.toDTO(savedComment);
    }

    @Override
    public List<CommentDTO> getCommentsByAdPk(Integer adPk) {
        // Проверяем, существует ли объявление
        adRepository.findById(adPk)
                .orElseThrow(() -> new AdNotFoundException("Ad not found"));

        List<Comment> comments = commentRepository.findByAdPk(adPk);
        return comments.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }


    @PreAuthorize("hasRole('ADMIN') or @commentSecurity.isCommentOwner(#id, authentication.name)")
    @Override
    @Transactional
    public CommentDTO updateComment(Integer pk, CommentDTO commentDTO) {
        Comment comment = commentRepository.findById(pk)
                .orElseThrow(() -> new CommentNotFoundException("Comment not found"));

        comment.setText(commentDTO.getText());

        Comment updatedComment = commentRepository.save(comment);
        return commentMapper.toDTO(updatedComment);
    }
    @PreAuthorize("hasRole('ADMIN') or @commentSecurity.isCommentOwner(#id, authentication.name)")
    @Override
    @Transactional
    public void deleteComment(Integer pk) {
        commentRepository.deleteById(pk); // Упрощение метода
    }
    private CommentDTO toDto(Comment comment) {
        CommentDTO dto = new CommentDTO();
        dto.setPk(comment.getPk()); // Установите id комментария
        dto.setText(comment.getText()); // Установите текст комментария
        dto.setCreatedAt(comment.getCreatedAt()); // Установите дату создания комментария

        // Проверяем, что user не равен null
        User user = comment.getUser (); // Используйте getUser () вместо getAuthor()
        if (user != null) {
            dto.setAuthor(user.getId()); // Установите id автора комментария
            dto.setAuthorImage(user.getImage()); // Установка ссылки на аватар (если есть)
            dto.setAuthorFirstName(user.getFirstName()); // Установка имени автора (если есть)
        }

        return dto;
    }

    @Override
    public List<CommentDTO> getCommentsByAd(Integer adPk) {
        List<Comment> comments = commentRepository.findByAdPk(adPk);
        return comments.stream()
                .map(commentMapper::toDTO) // Предполагается, что у вас есть метод для преобразования Comment в CommentDTO
                .collect(Collectors.toList());
    }



}
