package ru.practicum.shareit.comment;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CommentMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "item", ignore = true)
    @Mapping(target = "author", ignore = true)
    Comment toComment(CommentRequestDto requestDto);

    @Mapping(target = "itemId", source = "item.id")
    @Mapping(target = "authorId", source = "author.id")
    CommentResponseDto toResponseDto(Comment comment);

    @Mapping(target = "itemId", source = "item.id")
    @Mapping(target = "authorId", source = "author.id")
    CommentEntity toEntity(Comment comment);

    @Mapping(target = "item", ignore = true)
    @Mapping(target = "author", ignore = true)
    Comment toModel(CommentEntity commentEntity);
}
