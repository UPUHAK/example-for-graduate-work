package ru.skypro.homework.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.skypro.homework.model.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {

    Optional<User> findByEmail(String username);
    boolean existsByEmail(String email);
    Optional<Object> findById(Long userId);

    @Query("SELECT u FROM User u LEFT JOIN FETCH u.comments WHERE u.email = :email")
    User findByEmailWithComments(@Param("email") String email);

    @Query("SELECT u FROM User u LEFT JOIN FETCH u.comments WHERE u.id = :userId")
    User findByIdWithComments(@Param("userId") Integer userId);

}
