package ru.practicum.shareit.item;

import ru.practicum.shareit.user.User;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ItemMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "request", ignore = true)
    @Mapping(source = "requestDto.name", target = "name")
    @Mapping(source = "requestDto.description", target = "description")
    @Mapping(source = "requestDto.available", target = "available")
    @Mapping(source = "user", target = "user")
    Item toItem(ItemRequestDto requestDto, User user);

    ItemResponseDto toResponseDto(Item item);
}