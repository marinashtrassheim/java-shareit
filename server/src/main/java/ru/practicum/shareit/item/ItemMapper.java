package ru.practicum.shareit.item;

import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.comment.CommentMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.user.UserEntity;

@Mapper(componentModel = "spring",
        uses = {BookingMapper.class, CommentMapper.class})
public interface ItemMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "name", source = "inputDto.name")
    @Mapping(target = "description", source = "inputDto.description")
    @Mapping(target = "available", source = "inputDto.available")
    @Mapping(target = "owner", source = "owner")
    @Mapping(target = "request", ignore = true)
    @Mapping(target = "bookings", ignore = true)
    @Mapping(target = "comments", ignore = true)
    ItemEntity toEntity(ItemInputDto inputDto, UserEntity owner);

    @Mapping(target = "lastBooking", ignore = true)
    @Mapping(target = "nextBooking", ignore = true)
    @Mapping(target = "comments", source = "comments")
    @Mapping(target = "requestId", source = "request.id")
    @Mapping(target = "request", source = "request")
    ItemOutputDto toResponseDto(ItemEntity entity);

    @Mapping(target = "ownerId", source = "owner.id")
    ItemShortDto toShortDto(ItemEntity entity);

}
