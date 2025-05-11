package ru.skypro.homework.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.skypro.homework.dto.Comment;
import ru.skypro.homework.dto.Comments;
import ru.skypro.homework.dto.CreateOrUpdateComment;

import java.util.List;

@RestController
@RequestMapping("/ads/{adId}/comments")
public class CommentController {

    @GetMapping
    public ResponseEntity<Comments> getComments(@PathVariable Integer adId) {

        return ResponseEntity.ok(new Comments());
    }

    @PostMapping
    public ResponseEntity<Comment> addComment(@PathVariable Integer adId, @RequestBody CreateOrUpdateComment comment) {

        return ResponseEntity.ok(new Comment());
    }

    @PatchMapping("/{commentId}")
    public ResponseEntity<Comment> updateComment(@PathVariable Integer adId,
                                                 @PathVariable Integer commentId,
                                                 @RequestBody CreateOrUpdateComment comment) {

        return ResponseEntity.ok(new Comment());
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable Integer adId, @PathVariable Integer commentId) {

        return ResponseEntity.ok().build();
    }
}
