package ru.practicum.shareit;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.comment.CommentDto;
import ru.practicum.shareit.item.ItemInputDto;
import ru.practicum.shareit.item.ItemOutputDto;
import ru.practicum.shareit.item.ItemServerController;
import ru.practicum.shareit.item.ItemServiceImpl;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ItemServerController.class)
class ItemServerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ItemServiceImpl itemServiceImpl;

    @Test
    void createItem_shouldReturnCreated() throws Exception {
        ItemInputDto requestDto = ItemInputDto.builder()
                .name("Item")
                .description("Description")
                .available(true)
                .requestId(1L)
                .build();

        ItemOutputDto responseDto = ItemOutputDto.builder()
                .id(1L)
                .name("Item")
                .description("Description")
                .available(true)
                .build();

        when(itemServiceImpl.create(any(ItemInputDto.class), anyLong()))
                .thenReturn(responseDto);

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Item"));
    }

    @Test
    void getUserItems_shouldReturnItems() throws Exception {
        ItemOutputDto itemDto = ItemOutputDto.builder()
                .id(1L)
                .name("Item")
                .build();

        when(itemServiceImpl.getUserItems(anyLong(), anyInt(), anyInt()))
                .thenReturn(List.of(itemDto));

        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", 1)
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Item"));
    }

    @Test
    void getItemById_shouldReturnItem() throws Exception {
        ItemOutputDto itemDto = ItemOutputDto.builder()
                .id(1L)
                .name("Item")
                .build();

        when(itemServiceImpl.getById(anyLong(), anyLong()))
                .thenReturn(itemDto);

        mockMvc.perform(get("/items/1")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Item"));
    }

    @Test
    void updateItem_shouldReturnUpdatedItem() throws Exception {
        ItemInputDto requestDto = ItemInputDto.builder()
                .name("Updated")
                .build();

        ItemOutputDto responseDto = ItemOutputDto.builder()
                .id(1L)
                .name("Updated")
                .build();

        when(itemServiceImpl.update(anyLong(), any(ItemInputDto.class), anyLong()))
                .thenReturn(responseDto);

        mockMvc.perform(patch("/items/1")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Updated"));
    }

    @Test
    void searchItems_shouldReturnItems() throws Exception {
        ItemOutputDto itemDto = ItemOutputDto.builder()
                .id(1L)
                .name("Item")
                .build();

        when(itemServiceImpl.getItemsSearch(anyString(), anyInt(), anyInt()))
                .thenReturn(List.of(itemDto));

        mockMvc.perform(get("/items/search")
                        .param("text", "test")
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    void createComment_shouldReturnComment() throws Exception {
        CommentDto requestDto = CommentDto.builder()
                .text("Comment")
                .build();

        CommentDto responseDto = CommentDto.builder()
                .id(1L)
                .text("Comment")
                .build();

        when(itemServiceImpl.createComment(any(CommentDto.class), anyLong(), anyLong()))
                .thenReturn(responseDto);

        mockMvc.perform(post("/items/1/comment")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.text").value("Comment"));
    }

    @Test
    void searchItems_emptyText_shouldReturnEmptyList() throws Exception {
        when(itemServiceImpl.getItemsSearch(eq(""), anyInt(), anyInt()))
                .thenReturn(Collections.emptyList());

        mockMvc.perform(get("/items/search")
                        .param("text", "")
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }
}
