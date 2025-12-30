package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ItemRequestServerController.class)
class ItemRequestServerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ItemRequestService itemRequestService;

    @Test
    void createRequest_shouldReturnRequest() throws Exception {
        ItemRequestDto requestDto = ItemRequestDto.builder()
                .description("Need a drill")
                .build();

        ItemRequestDto responseDto = ItemRequestDto.builder()
                .id(1L)
                .description("Need a drill")
                .created(LocalDateTime.now())
                .build();

        when(itemRequestService.createRequest(any(ItemRequestDto.class), anyLong()))
                .thenReturn(responseDto);

        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.description").value("Need a drill"));
    }

    @Test
    void getUserRequests_shouldReturnRequests() throws Exception {
        ItemRequestDto requestDto = ItemRequestDto.builder()
                .id(1L)
                .description("Need a drill")
                .build();

        when(itemRequestService.getUserRequests(anyLong(), anyInt(), anyInt()))
                .thenReturn(List.of(requestDto));

        mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", 1)
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].description").value("Need a drill"));
    }

    @Test
    void getAllRequests_shouldReturnRequests() throws Exception {
        ItemRequestDto requestDto = ItemRequestDto.builder()
                .id(1L)
                .description("Need a drill")
                .build();

        when(itemRequestService.getAllRequests(anyInt(), anyInt()))
                .thenReturn(List.of(requestDto));

        mockMvc.perform(get("/requests/all")
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    void getRequestById_shouldReturnRequest() throws Exception {
        ItemRequestDto requestDto = ItemRequestDto.builder()
                .id(1L)
                .description("Need a drill")
                .build();

        when(itemRequestService.getRequestById(anyLong()))
                .thenReturn(requestDto);

        mockMvc.perform(get("/requests/1")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.description").value("Need a drill"));
    }

    @Test
    void getUserRequests_emptyResult_shouldReturnEmptyList() throws Exception {
        when(itemRequestService.getUserRequests(anyLong(), anyInt(), anyInt()))
                .thenReturn(Collections.emptyList());

        mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", 1)
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }
}