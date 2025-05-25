package ru.skypro.homework.test;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.skypro.homework.controller.AdController;
import ru.skypro.homework.dto.AdDTO;
import ru.skypro.homework.model.Ad;
import ru.skypro.homework.model.User;
import ru.skypro.homework.repository.AdRepository;
import ru.skypro.homework.repository.UserRepository;

import java.util.Optional;

public class AdControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private AdRepository adRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AdController adController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(adController).build();
    }

    @Test
    public void testAddAd() throws Exception {

        User user = new User();
        user.setId(1);
        AdDTO adDTO = new AdDTO();
        adDTO.setTitle("Test Ad");
        adDTO.setPrice(100);
        adDTO.setImage("image_url");
        adDTO.setAuthor(1);

        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(adRepository.save(any(Ad.class))).thenAnswer(invocation -> {
            Ad ad = invocation.getArgument(0);
            ad.setPk(1); // Эмулируем присвоение ID
            return ad;
        });


        mockMvc.perform(post("/ads")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"Test Ad\", \"price\":100.0, \"image\":\"image_url\", \"author\":1}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Test Ad"))
                .andExpect(jsonPath("$.price").value(100.0))
                .andExpect(jsonPath("$.author").value(1));


        verify(userRepository).findById(1);
        verify(adRepository).save(any(Ad.class));
    }

    @Test
    public void testGetAd() throws Exception {
        // Подготовка данных
        User user = new User();
        user.setId(1);

        Ad ad = new Ad();
        ad.setPk(1);
        ad.setTitle("Test Ad");
        ad.setPrice(100);
        ad.setImage("image_url");
        ad.setUser (user);


        when(adRepository.findById(1)).thenReturn(Optional.of(ad));


        mockMvc.perform(get("/ads/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pk").value(1))
                .andExpect(jsonPath("$.title").value("Test Ad"))
                .andExpect(jsonPath("$.price").value(100.0))
                .andExpect(jsonPath("$.image").value("image_url"))
                .andExpect(jsonPath("$.author").value(1));


        verify(adRepository).findById(1);
    }

    @Test
    public void testUpdateAd() throws Exception {

        User user = new User();
        user.setId(1);

        Ad existingAd = new Ad();
        existingAd.setPk(1);
        existingAd.setTitle("Old Title");
        existingAd.setPrice(100);
        existingAd.setImage("old_image_url");
        existingAd.setUser (user);


        when(adRepository.findById(1)).thenReturn(Optional.of(existingAd));
        when(adRepository.save(any(Ad.class))).thenAnswer(invocation -> invocation.getArgument(0));


        AdDTO updatedAdDTO = new AdDTO();
        updatedAdDTO.setTitle("Updated Title");
        updatedAdDTO.setPrice(150);
        updatedAdDTO.setImage("updated_image_url");
        updatedAdDTO.setAuthor(1);


        mockMvc.perform(put("/ads/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"Updated Title\", \"price\":150.0, \"image\":\"updated_image_url\", \"author\":1}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pk").value(1))
                .andExpect(jsonPath("$.title").value("Updated Title"))
                .andExpect(jsonPath("$.price").value(150.0))
                .andExpect(jsonPath("$.image").value("updated_image_url"))
                .andExpect(jsonPath("$.author").value(1));


        verify(adRepository).findById(1);
        verify(adRepository).save(any(Ad.class));
    }
    @Test
    public void testDeleteAd() throws Exception {
        /*
         Настраиваем мок репозитория: объявление с id=1 существует
         */
        when(adRepository.existsById(1)).thenReturn(true);
        doNothing().when(adRepository).deleteById(1);

        /*
         Выполняем DELETE-запрос
         */
        mockMvc.perform(delete("/ads/1"))
                .andExpect(status().isNoContent());


        verify(adRepository).existsById(1);
        verify(adRepository).deleteById(1);
    }
    @Test
    public void testDeleteAd_NotFound() throws Exception {
        /*
         Настраиваем мок репозитория: объявление с id=1 не существует
         */
        when(adRepository.existsById(1)).thenReturn(false);


        mockMvc.perform(delete("/ads/1"))
                .andExpect(status().isNotFound());

        verify(adRepository).existsById(1);
        verify(adRepository, never()).deleteById(anyInt());
    }

}

