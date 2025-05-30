package ru.skypro.homework.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.skypro.homework.dto.CommentDTO;
import ru.skypro.homework.dto.CommentsDTO;
import ru.skypro.homework.dto.CreateOrUpdateCommentDTO;


@CrossOrigin(value = "http://localhost:3000")
@RestController
@RequestMapping("/ads/{adId}/comments")
public class CommentController {

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<CommentsDTO> getComments(@PathVariable Integer adId) {

        return ResponseEntity.ok(new CommentsDTO());
    }

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<CommentDTO> addComment(@PathVariable Integer adId
            , @RequestBody CreateOrUpdateCommentDTO comment) {

        return ResponseEntity.ok(new CommentDTO());
    }

    @PatchMapping("/{commentId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<CommentDTO> updateComment(@PathVariable Integer adId,
                                                    @PathVariable Integer commentId,
                                                    @RequestBody CreateOrUpdateCommentDTO comment) {

        return ResponseEntity.ok(new CommentDTO());
    }

    @DeleteMapping("/{commentId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteComment(@PathVariable Integer adId, @PathVariable Integer commentId) {

        return ResponseEntity.ok().build();
    }
}
