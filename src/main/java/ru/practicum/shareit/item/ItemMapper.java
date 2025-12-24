package ru.practicum.shareit.item;

import org.mapstruct.MappingTarget;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.comment.CommentMapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.user.UserEntity;

@Mapper(componentModel = "spring",
        uses = {BookingMapper.class, CommentMapper.class})
public interface ItemMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "name", source = "requestDto.name")
    @Mapping(target = "description", source = "requestDto.description")
    @Mapping(target = "available", source = "requestDto.available")
    @Mapping(target = "owner", source = "owner")
    @Mapping(target = "requestId", source = "requestDto.requestId")
    @Mapping(target = "bookings", ignore = true)
    @Mapping(target = "comments", ignore = true)
    ItemEntity toEntity(ItemRequestDto requestDto, UserEntity owner);


    @Mapping(target = "lastBooking", ignore = true)
    @Mapping(target = "nextBooking", ignore = true)
    @Mapping(target = "comments", source = "comments")
    ItemResponseDto toResponseDto(ItemEntity entity);


    @Mapping(target = "id", ignore = true)
    @Mapping(target = "owner", ignore = true)
    @Mapping(target = "requestId", ignore = true)
    @Mapping(target = "bookings", ignore = true)
    @Mapping(target = "comments", ignore = true)
    void updateEntityFromDto(ItemRequestDto requestDto, @MappingTarget ItemEntity entity);

}
