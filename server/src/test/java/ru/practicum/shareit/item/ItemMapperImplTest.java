package ru.practicum.shareit.item;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.comment.CommentMapper;
import ru.practicum.shareit.comment.CommentEntity;
import ru.practicum.shareit.comment.CommentDto;
import ru.practicum.shareit.request.ItemRequestEntity;
import ru.practicum.shareit.user.UserEntity;

import java.util.List;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemMapperImplTest {

    private ItemMapperImpl mapper;

    @Mock
    private CommentMapper commentMapper;
    @Mock
    private BookingMapper bookingMapper;

    @BeforeEach
    void setUp() {
        mapper = new ItemMapperImpl(commentMapper);
    }

    @Test
    void toEntity_whenInputDtoAndOwnerProvided() {
        ItemInputDto inputDto = new ItemInputDto();
        inputDto.setName("Item");
        inputDto.setDescription("Description");
        inputDto.setAvailable(true);

        UserEntity owner = new UserEntity();
        owner.setId(1L);

        ItemEntity result = mapper.toEntity(inputDto, owner);

        assertEquals("Item", result.getName());
        assertEquals("Description", result.getDescription());
        assertTrue(result.getAvailable());
        assertEquals(owner, result.getOwner());
    }

    @Test
    void toEntity_whenNullInput() {
        assertNull(mapper.toEntity(null, null));
    }

    @Test
    void toShortDto_whenEntityHasOwner() {
        ItemEntity entity = new ItemEntity();
        entity.setId(1L);
        entity.setName("Item");

        UserEntity owner = new UserEntity();
        owner.setId(2L);
        entity.setOwner(owner);

        ItemShortDto result = mapper.toShortDto(entity);

        assertEquals(1L, result.getId());
        assertEquals("Item", result.getName());
        assertEquals(2L, result.getOwnerId());
    }

    @Test
    void toShortDto_whenEntityHasNoOwner() {
        ItemEntity entity = new ItemEntity();
        entity.setId(1L);
        entity.setName("Item");

        ItemShortDto result = mapper.toShortDto(entity);

        assertEquals(1L, result.getId());
        assertEquals("Item", result.getName());
        assertNull(result.getOwnerId());
    }

    @Test
    void toResponseDto_whenEntityNull() {
        assertNull(mapper.toResponseDto(null));
    }

    @Test
    void toShortDto_whenEntityNull() {
        assertNull(mapper.toShortDto(null));
    }

    @Test
    void toResponseDto_whenEntityHasCommentsAndRequest() {
        ItemEntity entity = new ItemEntity();
        entity.setId(1L);
        entity.setName("Item");

        CommentEntity comment = new CommentEntity();
        CommentDto commentDto = new CommentDto();
        when(commentMapper.toResponseDto(comment)).thenReturn(commentDto);
        entity.setComments(List.of(comment));

        ItemRequestEntity request = new ItemRequestEntity();
        request.setId(2L);
        request.setDescription("Request description");


        ItemEntity itemInRequest = new ItemEntity();
        itemInRequest.setId(3L);
        itemInRequest.setName("Item in request");
        request.setItems(List.of(itemInRequest));

        entity.setRequest(request);

        ItemOutputDto result = mapper.toResponseDto(entity);

        assertEquals(1L, result.getId());
        assertEquals("Item", result.getName());


        assertNotNull(result.getComments());
        assertEquals(1, result.getComments().size());
        verify(commentMapper).toResponseDto(comment);


        assertNotNull(result.getRequest());
        assertEquals(2L, result.getRequestId());
        assertNotNull(result.getRequest().getItems());
        assertEquals(1, result.getRequest().getItems().size());
        assertEquals(3L, result.getRequest().getItems().get(0).getId());
        assertEquals("Item in request", result.getRequest().getItems().get(0).getName());
    }

    @Test
    void toResponseDto_whenEntityHasNullCollections() {
        ItemEntity entity = new ItemEntity();
        entity.setId(1L);
        entity.setName("Item");

        ItemOutputDto result = mapper.toResponseDto(entity);

        assertEquals(1L, result.getId());
        assertEquals("Item", result.getName());
        assertNull(result.getComments());
        assertNull(result.getRequest());
    }
}