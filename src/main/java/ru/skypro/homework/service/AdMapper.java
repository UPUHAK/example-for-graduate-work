package ru.skypro.homework.service;

import org.springframework.stereotype.Component;
import ru.skypro.homework.dto.AdDTO;
import ru.skypro.homework.model.Ad;

@Component
public class AdMapper {
    public AdDTO convertToDTO(Ad ad) {
        AdDTO adDTO = new AdDTO();
        adDTO.setPk(ad.getPk());
        adDTO.setTitle(ad.getTitle());
        adDTO.setPrice(ad.getPrice());
        adDTO.setImage(ad.getImage());
        adDTO.setAuthor(ad.getUser ().getId());
        return adDTO;
    }
}
