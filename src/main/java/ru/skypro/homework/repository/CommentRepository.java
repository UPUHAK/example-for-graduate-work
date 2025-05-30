package ru.skypro.homework.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.skypro.homework.model.Comment;
import ru.skypro.homework.model.User;

public interface CommentRepository extends JpaRepository<Comment, Integer> {


    long countByAdPk(Integer adPk);

    long countByUserId(Integer userId);

    @Query("SELECT u FROM User u LEFT JOIN FETCH u.comments WHERE u.id = :id")
    User findByIdWithComments(@Param("id") Integer id);



}
