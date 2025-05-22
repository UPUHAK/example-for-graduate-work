package ru.skypro.homework.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.skypro.homework.dto.AdDTO;
import ru.skypro.homework.exception.AdNotFoundException;
import ru.skypro.homework.exception.UserNotFoundException;
import ru.skypro.homework.model.Ad;
import ru.skypro.homework.model.User;
import ru.skypro.homework.repository.AdRepository;
import ru.skypro.homework.repository.UserRepository;
import ru.skypro.homework.service.AdMapper;
import ru.skypro.homework.service.AdService;

@Service
@RequiredArgsConstructor
public class AdServiceImpl implements AdService {

    private final AdRepository adRepository;
    private final UserRepository userRepository;
    private final AdMapper adMapper;

    @Override
    @Transactional
    public AdDTO addAd(AdDTO adDTO) {
        User user = userRepository.findById(adDTO.getAuthor())
                .orElseThrow(() -> new UserNotFoundException("User  not found"));

        Ad ad = new Ad();
        ad.setTitle(adDTO.getTitle());
        ad.setPrice(adDTO.getPrice());
        ad.setImage(adDTO.getImage());
        ad.setUser (user);

        Ad savedAd = adRepository.save(ad);
        return adMapper.convertToDTO(savedAd);
    }

    @Override
    @Transactional(readOnly = true)
    public AdDTO getAd(Integer id) {
        Ad ad = adRepository.findById(id)
                .orElseThrow(() -> new AdNotFoundException("Ad not found"));
        return adMapper.convertToDTO(ad);
    }

    @PreAuthorize("hasRole('ADMIN') or @adSecurity.isAdOwner(#id, authentication.name)")
    @Override
    @Transactional
    public AdDTO updateAd(Integer id, AdDTO adDTO) {
        Ad ad = adRepository.findById(id)
                .orElseThrow(() -> new AdNotFoundException("Ad not found"));

        ad.setTitle(adDTO.getTitle());
        ad.setPrice(adDTO.getPrice());
        ad.setImage(adDTO.getImage());

        Ad updatedAd = adRepository.save(ad);
        return adMapper.convertToDTO(updatedAd);
    }

    @PreAuthorize("hasRole('ADMIN') or @adSecurity.isAdOwner(#id, authentication.name)")
    @Override
    @Transactional
    public void deleteAd(Integer id) {
        if (!adRepository.existsById(id)) {
            throw new AdNotFoundException("Ad not found");
        }
        adRepository.deleteById(id);
    }

}

