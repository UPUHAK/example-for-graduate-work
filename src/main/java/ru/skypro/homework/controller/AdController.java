package ru.skypro.homework.controller;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.skypro.homework.dto.AdDTO;
import ru.skypro.homework.exception.AdNotFoundException;
import ru.skypro.homework.exception.UserNotFoundException;
import ru.skypro.homework.model.Ad;
import ru.skypro.homework.model.User;
import ru.skypro.homework.repository.AdRepository;
import ru.skypro.homework.repository.UserRepository;

import javax.validation.Valid;
import java.util.Arrays;
import java.util.List;


@Slf4j
@CrossOrigin(value = "http://localhost:3000")
@RestController
@RequestMapping("/ads")
@RequiredArgsConstructor
public class AdController {

    private final AdRepository adRepository;
    private final UserRepository userRepository;

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<AdDTO> addAd(@Valid @RequestBody AdDTO adDTO) {
        log.info("Adding new ad: {}", adDTO);

        User user = userRepository.findById(adDTO.getAuthor())
                .orElseThrow(() -> new UserNotFoundException("User  not found"));

        Ad ad = new Ad();
        ad.setTitle(adDTO.getTitle());
        ad.setPrice(adDTO.getPrice());
        ad.setImage(adDTO.getImage());
        ad.setUser (user);

        Ad savedAd = adRepository.save(ad);
        AdDTO savedAdDTO = convertToDTO(savedAd);

        return ResponseEntity.status(HttpStatus.CREATED).body(savedAdDTO);
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<AdDTO> getAd(@PathVariable Integer id) {
        log.info("Fetching ad with id: {}", id);

        Ad ad = adRepository.findById(id)
                .orElseThrow(() -> new AdNotFoundException("Ad not found"));

        AdDTO adDTO = convertToDTO(ad);
        return ResponseEntity.ok(adDTO);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or (isAuthenticated() and @adSecurity.isOwner(#id, authentication))")
    public ResponseEntity<AdDTO> updateAd(@PathVariable Integer id, @Valid @RequestBody AdDTO adDTO) {
        log.info("Updating ad with id: {}", id);

        Ad ad = adRepository.findById(id)
                .orElseThrow(() -> new AdNotFoundException("Ad not found"));

        ad.setTitle(adDTO.getTitle());
        ad.setPrice(adDTO.getPrice());
        ad.setImage(adDTO.getImage());

        Ad updatedAd = adRepository.save(ad);
        AdDTO updatedAdDTO = convertToDTO(updatedAd);

        return ResponseEntity.ok(updatedAdDTO);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or (isAuthenticated() and @adSecurity.isOwner(#id, authentication))")
    public ResponseEntity<Void> deleteAd(@PathVariable Integer id) {
        log.info("Deleting ad with id: {}", id);

        if (!adRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        adRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }


    private AdDTO convertToDTO(Ad ad) {
        AdDTO adDTO = new AdDTO();
        adDTO.setPk(ad.getPk());
        adDTO.setTitle(ad.getTitle());
        adDTO.setPrice(ad.getPrice());
        adDTO.setImage(ad.getImage());
        adDTO.setAuthor(ad.getUser ().getId());
        return adDTO;
    }
}



