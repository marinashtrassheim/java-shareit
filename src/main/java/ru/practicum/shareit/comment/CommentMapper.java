package ru.practicum.shareit.comment;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.item.ItemEntity;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.user.UserEntity;
import ru.practicum.shareit.user.UserMapper;

@Mapper(componentModel = "spring",
        uses = {ItemMapper.class, UserMapper.class})
public interface CommentMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "item", source = "item")
    @Mapping(target = "author", source = "author")
    @Mapping(target = "created", expression = "java(java.time.LocalDateTime.now())")
    CommentEntity toEntity(CommentRequestDto requestDto,
                           ItemEntity item,
                           UserEntity author);
    @Mapping(target = "itemId", source = "item.id")
    @Mapping(target = "authorId", source = "author.id")
    @Mapping(target = "authorName", source = "author.name")
    CommentResponseDto toResponseDto(CommentEntity entity);
}
