package ru.practicum.shareit;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.BookingDto;
import ru.practicum.shareit.booking.BookingEntity;
import ru.practicum.shareit.booking.BookingMapperImpl;
import ru.practicum.shareit.booking.BookingShortDto;
import ru.practicum.shareit.item.ItemEntity;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.user.UserEntity;
import ru.practicum.shareit.user.UserMapper;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class BookingMapperImplTest {

    @Mock
    private UserMapper userMapper;
    @Mock
    private ItemMapper itemMapper;

    @Test
    void toDto() {
        BookingMapperImpl mapper = new BookingMapperImpl(userMapper, itemMapper);

        BookingEntity entity = new BookingEntity();
        entity.setStartDate(LocalDateTime.now());
        entity.setEndDate(LocalDateTime.now().plusDays(1));

        UserEntity booker = new UserEntity();
        booker.setId(1L);
        entity.setBooker(booker);

        ItemEntity item = new ItemEntity();
        item.setId(2L);
        entity.setItem(item);

        BookingDto result = mapper.toDto(entity);

        assertEquals(entity.getStartDate(), result.getStart());
        assertEquals(entity.getEndDate(), result.getEnd());
    }

    @Test
    void toShortDto() {
        BookingMapperImpl mapper = new BookingMapperImpl(userMapper, itemMapper);

        BookingEntity entity = new BookingEntity();
        entity.setId(1L);

        UserEntity booker = new UserEntity();
        booker.setId(2L);
        entity.setBooker(booker);

        BookingShortDto result = mapper.toShortDto(entity);

        assertEquals(1L, result.getId());
        assertEquals(2L, result.getBookerId());
    }

    @Test
    void toEntity() {
        BookingMapperImpl mapper = new BookingMapperImpl(userMapper, itemMapper);

        BookingDto dto = new BookingDto();
        dto.setStart(LocalDateTime.now());
        dto.setEnd(LocalDateTime.now().plusDays(1));

        BookingEntity result = mapper.toEntity(dto);

        assertEquals(dto.getStart(), result.getStartDate());
        assertEquals(dto.getEnd(), result.getEndDate());
        assertNull(result.getId());
        assertNull(result.getItem());
        assertNull(result.getBooker());
        assertNull(result.getStatus());
    }

    @Test
    void toDto_whenEntityNull() {
        BookingMapperImpl mapper = new BookingMapperImpl(userMapper, itemMapper);
        assertNull(mapper.toDto(null));
    }

    @Test
    void toShortDto_whenEntityNull() {
        BookingMapperImpl mapper = new BookingMapperImpl(userMapper, itemMapper);
        assertNull(mapper.toShortDto(null));
    }

    @Test
    void toEntity_whenDtoNull() {
        BookingMapperImpl mapper = new BookingMapperImpl(userMapper, itemMapper);
        assertNull(mapper.toEntity(null));
    }

    @Test
    void toShortDto_whenBookerNull() {
        BookingMapperImpl mapper = new BookingMapperImpl(userMapper, itemMapper);
        BookingEntity entity = new BookingEntity();
        entity.setId(1L);
        BookingShortDto result = mapper.toShortDto(entity);
        assertEquals(1L, result.getId());
        assertNull(result.getBookerId());
    }

    @Test
    void toShortDto_whenBookerIdNull() {
        BookingMapperImpl mapper = new BookingMapperImpl(userMapper, itemMapper);
        BookingEntity entity = new BookingEntity();
        entity.setId(1L);
        UserEntity booker = new UserEntity();
        entity.setBooker(booker);
        BookingShortDto result = mapper.toShortDto(entity);
        assertEquals(1L, result.getId());
        assertNull(result.getBookerId());
    }
}