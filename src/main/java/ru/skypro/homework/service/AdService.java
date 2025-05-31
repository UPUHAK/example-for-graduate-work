package ru.skypro.homework.service;

import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.dto.AdDTO;

import java.util.List;

public interface AdService {
    List<AdDTO> getAllAds();
    AdDTO addAd(AdDTO adDTO);
    AdDTO getAdById(Integer id);
    void deleteAd(Integer id);
    AdDTO updateAd(Integer id, AdDTO adDTO);
    List<AdDTO> getAdsByAuthor(String username);
    AdDTO updateAdImage(Integer id, MultipartFile image);
}


