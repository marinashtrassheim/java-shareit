package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import ru.practicum.shareit.item.ItemMapper;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ItemRequestMapperImplTest {

    @Mock
    private ItemMapper itemMapper;

    private ItemRequestMapper itemRequestMapper;

    @BeforeEach
    void setUp() {
        itemRequestMapper = new ItemRequestMapperImpl(itemMapper);
    }

    @Test
    void toEntity() {
        ItemRequestDto dto = new ItemRequestDto();
        dto.setDescription("Need item");

        ItemRequestEntity result = itemRequestMapper.toEntity(dto);

        assertEquals("Need item", result.getDescription());
        assertNull(result.getId());
        assertNull(result.getItems());
        assertNull(result.getRequester());
        assertNull(result.getCreated());
    }

    @Test
    void toEntity_whenNull() {
        assertNull(itemRequestMapper.toEntity(null));
    }

    @Test
    void toResponseDto() {
        ItemRequestEntity entity = new ItemRequestEntity();
        entity.setId(1L);
        entity.setDescription("Need item");

        ItemRequestDto result = itemRequestMapper.toResponseDto(entity);

        assertEquals(1L, result.getId());
        assertEquals("Need item", result.getDescription());
    }

    @Test
    void toResponseDto_whenEntityNull() {
        assertNull(itemRequestMapper.toResponseDto(null));
    }

    @Test
    void toResponseDto_whenItemsNull() {
        ItemRequestEntity entity = new ItemRequestEntity();
        entity.setId(1L);

        ItemRequestDto result = itemRequestMapper.toResponseDto(entity);

        assertEquals(1L, result.getId());
        assertNull(result.getItems());
    }

    @Test
    void toResponseDto_whenItemsEmpty() {
        ItemRequestEntity entity = new ItemRequestEntity();
        entity.setId(1L);
        entity.setItems(List.of());

        ItemRequestDto result = itemRequestMapper.toResponseDto(entity);

        assertEquals(1L, result.getId());
        assertNotNull(result.getItems());
        assertTrue(result.getItems().isEmpty());
    }
}