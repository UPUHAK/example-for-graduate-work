package ru.skypro.homework.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import ru.skypro.homework.exception.UserNotFoundException;
import ru.skypro.homework.model.User;
import ru.skypro.homework.repository.UserRepository;
@Slf4j
@Component
@RequiredArgsConstructor
public class CurrentUserService {

    @Autowired
    private  UserRepository userRepository;

    /**
     * Получает текущего аутентифицированного пользователя.
     *
     * @return текущий пользователь
     * @throws UserNotFoundException если пользователь не аутентифицирован или не найден
     */
    public User getAuthenticatedUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || auth instanceof AnonymousAuthenticationToken) {
            log.warn("Пользователь не аутентифицирован");
            throw new UserNotFoundException("Пользователь не аутентифицирован");
        }
        String username = auth.getName();
        log.info("Получаем пользователя по username: {}", username);
        return userRepository.findByEmail(username)
                .orElseThrow(() -> new UserNotFoundException("Пользователь с именем " + username + " не найден"));
    }


}
