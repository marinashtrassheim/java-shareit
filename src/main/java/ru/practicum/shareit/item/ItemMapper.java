package ru.practicum.shareit.item;

import ru.practicum.shareit.user.User;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ItemMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "name", source = "requestDto.name")
    @Mapping(target = "request", ignore = true)
    @Mapping(target = "requestId", ignore = true)
    @Mapping(target = "owner", source = "user")
    @Mapping(target = "ownerId", source = "user.id")
    Item toItem(ItemRequestDto requestDto, User user);

    @Mapping(target = "lastBooking", ignore = true)
    @Mapping(target = "nextBooking", ignore = true)
    @Mapping(target = "comments", ignore = true)
    ItemResponseDto toResponseDto(Item item);

    ItemEntity toEntity(Item item);

    @Mapping(target = "ownerId", source = "ownerId")
    @Mapping(target = "owner", ignore = true)
    @Mapping(target = "request", ignore = true)
    Item toModel(ItemEntity entity);

}
