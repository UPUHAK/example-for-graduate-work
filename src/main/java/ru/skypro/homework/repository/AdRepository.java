package ru.skypro.homework.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.skypro.homework.model.Ad;

import java.util.List;
import java.util.Optional;

public interface AdRepository extends JpaRepository<Ad, Integer> {

    @Override
    Optional<Ad> findById(Integer id); // Переопределяем метод, чтобы вернуть Optional<Ad>

    void deleteById(Integer id);

    List<Ad> findByUserEmail(String email);

}
