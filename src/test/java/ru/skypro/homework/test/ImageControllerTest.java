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

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@WebMvcTest(ImageController.class)
public class ImageControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private ImageService imageService; // Изменено на интерфейс

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetAllImages() throws Exception {
        // Подготовка данных для теста
        Image image1 = new Image(1, "image1.png", new byte[0], null, null);
        Image image2 = new Image(2, "image2.png", new byte[0], null, null);
        List<Image> images = Arrays.asList(image1, image2);

        when(imageService.getAllImages()).thenReturn(images);

        // Выполнение запроса и проверка результата
        mockMvc.perform(get("/api/images")

                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[1].id").value(2));
    }

    @Test
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
    public void testGetImageByIdNotFound() throws Exception {
        // Подготовка данных для теста
        when(imageService.getImageById(1)).thenReturn(Optional.empty());

        // Выполнение запроса и проверка результата
        mockMvc.perform(get("/api/images/1") // Изменено на правильный путь
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}


