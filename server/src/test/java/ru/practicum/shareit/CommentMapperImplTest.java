package ru.practicum.shareit;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.comment.CommentDto;
import ru.practicum.shareit.comment.CommentEntity;
import ru.practicum.shareit.comment.CommentMapperImpl;
import ru.practicum.shareit.item.ItemEntity;
import ru.practicum.shareit.user.UserEntity;


import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class CommentMapperImplTest {

    @Test
    void toEntity() {
        CommentMapperImpl mapper = new CommentMapperImpl();

        CommentDto commentDto = new CommentDto();
        commentDto.setText("Text");

        ItemEntity item = new ItemEntity();
        UserEntity author = new UserEntity();

        CommentEntity result = mapper.toEntity(commentDto, item, author);

        assertEquals("Text", result.getText());
        assertEquals(item, result.getItem());
        assertEquals(author, result.getAuthor());
        assertNotNull(result.getCreated());
    }

    @Test
    void toEntity_whenNullInput() {
        CommentMapperImpl mapper = new CommentMapperImpl();
        assertNull(mapper.toEntity(null, null, null));
    }

    @Test
    void toResponseDto() {
        CommentMapperImpl mapper = new CommentMapperImpl();

        CommentEntity entity = new CommentEntity();
        entity.setText("Text");

        ItemEntity item = new ItemEntity();
        item.setId(1L);
        entity.setItem(item);

        UserEntity author = new UserEntity();
        author.setId(2L);
        author.setName("Author");
        entity.setAuthor(author);

        CommentDto result = mapper.toResponseDto(entity);

        assertEquals("Text", result.getText());
        assertEquals(1L, result.getItemId());
        assertEquals(2L, result.getAuthorId());
        assertEquals("Author", result.getAuthorName());
    }

    @Test
    void toResponseDto_whenEntityNull() {
        CommentMapperImpl mapper = new CommentMapperImpl();
        assertNull(mapper.toResponseDto(null));
    }

    @Test
    void toResponseDto_whenItemNull() {
        CommentMapperImpl mapper = new CommentMapperImpl();

        CommentEntity entity = new CommentEntity();
        entity.setText("Text");


        UserEntity author = new UserEntity();
        author.setId(2L);
        author.setName("Author");
        entity.setAuthor(author);

        CommentDto result = mapper.toResponseDto(entity);

        assertEquals("Text", result.getText());
        assertNull(result.getItemId());
        assertEquals(2L, result.getAuthorId());
        assertEquals("Author", result.getAuthorName());
    }

    @Test
    void toResponseDto_whenAuthorNull() {
        CommentMapperImpl mapper = new CommentMapperImpl();

        CommentEntity entity = new CommentEntity();
        entity.setText("Text");

        ItemEntity item = new ItemEntity();
        item.setId(1L);
        entity.setItem(item);


        CommentDto result = mapper.toResponseDto(entity);

        assertEquals("Text", result.getText());
        assertEquals(1L, result.getItemId());
        assertNull(result.getAuthorId());
        assertNull(result.getAuthorName());
    }

    @Test
    void toResponseDto_whenAuthorNameNull() {
        CommentMapperImpl mapper = new CommentMapperImpl();

        CommentEntity entity = new CommentEntity();
        entity.setText("Text");

        ItemEntity item = new ItemEntity();
        item.setId(1L);
        entity.setItem(item);

        UserEntity author = new UserEntity();
        author.setId(2L);
        entity.setAuthor(author);

        CommentDto result = mapper.toResponseDto(entity);

        assertEquals("Text", result.getText());
        assertEquals(1L, result.getItemId());
        assertEquals(2L, result.getAuthorId());
        assertNull(result.getAuthorName());
    }

    @Test
    void toEntity_createdShouldBeSet() {
        CommentMapperImpl mapper = new CommentMapperImpl();

        CommentDto commentDto = new CommentDto();
        ItemEntity item = new ItemEntity();
        UserEntity author = new UserEntity();

        CommentEntity result = mapper.toEntity(commentDto, item, author);

        assertNotNull(result.getCreated());
        assertTrue(result.getCreated().isBefore(LocalDateTime.now().plusSeconds(1)) ||
                result.getCreated().isEqual(LocalDateTime.now()));
    }
}