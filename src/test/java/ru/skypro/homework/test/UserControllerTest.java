package ru.skypro.homework.test;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.controller.UserController;
import ru.skypro.homework.dto.NewPasswordDTO;
import ru.skypro.homework.dto.Role;
import ru.skypro.homework.dto.UpdateUserDTO;
import ru.skypro.homework.dto.UserDTO;
import ru.skypro.homework.service.UserService;


public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
    }

    @Test
    public void testSetPassword() throws Exception {
        NewPasswordDTO newPasswordDTO = new NewPasswordDTO("currentPass123", "newPassword123");


        doNothing().when(userService).setPassword(any(NewPasswordDTO.class));


        String json = new ObjectMapper().writeValueAsString(newPasswordDTO);

        mockMvc.perform(post("/users/set_password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk());

        verify(userService, times(1)).setPassword(any(NewPasswordDTO.class));
    }


    @Test
    public void testGetUser () throws Exception {
        UserDTO user = new UserDTO(1, "user@example.com","password","user@example.com", "User ", "Name", "+7 123 456-78-90", Role.USER, "link_to_avatar");
        when(userService.getCurrentUser ()).thenReturn(user);

        mockMvc.perform(get("/users/me"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("user@example.com"))
                .andExpect(jsonPath("$.firstName").value("User ")) // Исправлено на firstName
                .andExpect(jsonPath("$.lastName").value("Name")); // Исправлено на lastName

        verify(userService, times(1)).getCurrentUser ();
    }

    @Test
    public void testUpdateUser () throws Exception {
        UpdateUserDTO updateUser  = new UpdateUserDTO("Имя", "Фамилия", "+7 123 456-78-90");
        UserDTO updatedUser  = new UserDTO(1, "newUser@example.com","password","newEmail@example.com", "Имя", "Фамилия", "+7 123 456-78-90", Role.USER, "link_to_avatar");


        when(userService.getCurrentUser ()).thenReturn(updatedUser );
        when(userService.updateUser (any(UpdateUserDTO.class))).thenReturn(updatedUser );


        mockMvc.perform(patch("/users/me")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"firstName\":\"Имя\",\"lastName\":\"Фамилия\",\"phone\":\"+7 123 456-78-90\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("newEmail@example.com"))
                .andExpect(jsonPath("$.firstName").value("Имя"))
                .andExpect(jsonPath("$.lastName").value("Фамилия"));


        verify(userService, times(1)).updateUser (any(UpdateUserDTO.class));
    }


}


