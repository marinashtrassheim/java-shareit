package ru.practicum.shareit.user;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "bookings", ignore = true)
    @Mapping(target = "comments", ignore = true)
    @Mapping(target = "requests", ignore = true)
    @Mapping(target = "items", ignore = true)
    UserEntity toEntity(UserDto dto);

    UserDto toDto(UserEntity entity);

}