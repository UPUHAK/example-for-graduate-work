package ru.skypro.homework.test;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import ru.skypro.homework.controller.ImageController;
import ru.skypro.homework.model.Image;
import ru.skypro.homework.service.ImageService; // Используйте интерфейс
import org.springframework.boot.test.mock.mockito.MockBean;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import static org.mockito.BDDMockito.when;

@WebMvcTest(ImageController.class)
public class ImageControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ImageService imageService;

    @BeforeEach
    public void setUp() {
        List<Image> mockImages = Arrays.asList(new Image(1, "image1.png", new byte[0], null, null));
        when(imageService.getAllImages()).thenReturn(mockImages);
    }


    @Test
    @WithMockUser (roles = "USER")
    public void testGetImageByIdFound() throws Exception {
        // Подготовка данных для теста
        Image image = new Image(1, "image.png", new byte[0], null, null);

        when(imageService.getImageById(1)).thenReturn(Optional.of(image));

        // Выполнение запроса и проверка результата
        mockMvc.perform(get("/api/images/1") // Изменено на правильный путь
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @WithMockUser
    public void testGetImageByIdNotFound() throws Exception {

        when(imageService.getImageById(1)).thenReturn(Optional.empty());


        mockMvc.perform(get("/api/images/1") // Изменено на правильный путь
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}


