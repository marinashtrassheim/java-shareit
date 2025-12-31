package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserServerController.class)
class UserServerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @Test
    void createUser_shouldReturnCreated() throws Exception {

        UserDto requestDto = UserDto.builder()
                .name("John")
                .email("john@example.com")
                .build();

        UserDto responseDto = UserDto.builder()
                .id(1L)
                .name("John")
                .email("john@example.com")
                .build();

        when(userService.create(any(UserDto.class))).thenReturn(responseDto);


        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("John"))
                .andExpect(jsonPath("$.email").value("john@example.com"));
    }

    @Test
    void getUser_shouldReturnUser() throws Exception {

        UserDto userDto = UserDto.builder()
                .id(1L)
                .name("John")
                .email("john@example.com")
                .build();

        when(userService.get(1L)).thenReturn(userDto);


        mockMvc.perform(get("/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("John"))
                .andExpect(jsonPath("$.email").value("john@example.com"));
    }

    @Test
    void updateUser_shouldReturnUpdatedUser() throws Exception {

        UserDto requestDto = UserDto.builder()
                .name("Updated")
                .email("updated@example.com")
                .build();

        UserDto responseDto = UserDto.builder()
                .id(1L)
                .name("Updated")
                .email("updated@example.com")
                .build();

        when(userService.update(any(UserDto.class), eq(1L))).thenReturn(responseDto);


        mockMvc.perform(patch("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Updated"))
                .andExpect(jsonPath("$.email").value("updated@example.com"));
    }

    @Test
    void deleteUser_shouldReturnNoContent() throws Exception {

        mockMvc.perform(delete("/users/1"))
                .andExpect(status().isNoContent());
    }
}