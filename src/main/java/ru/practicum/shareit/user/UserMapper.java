package ru.practicum.shareit.user;

import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

    User toUser(UserDto userDto);

    User toModel(UserEntity userEntity);
}