package ru.skypro.homework.service;

import ru.skypro.homework.dto.AdDTO;

public interface AdService {
    AdDTO addAd(AdDTO adDTO);
    AdDTO getAd(Integer id);
    AdDTO updateAd(Integer id, AdDTO adDTO);
    void deleteAd(Integer id);
}

