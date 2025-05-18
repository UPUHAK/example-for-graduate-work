package ru.skypro.homework.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.skypro.homework.model.Comment;

public interface CommentRepository extends JpaRepository<Comment, Integer> {


    @Query("SELECT COUNT(c) FROM Comment c WHERE c.ad.pk = :adId")
    long countByAdId(@Param("adId") Integer adId);


    @Query("SELECT COUNT(c) FROM Comment c WHERE c.user.id = :userId")
    long countByUserId(@Param("userId") Long userId);
}
