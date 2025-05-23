package ru.skypro.homework.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.skypro.homework.repository.AdRepository;

@Component("adSecurity")
public class AdSecurity {

    @Autowired
    private AdRepository adRepository;

    public boolean isAdOwner(Integer adId, String username) {
        return adRepository.findById(adId)
                .map(ad -> ad.getUser().getUsername().equals(username))
                .orElse(false);
    }
}
