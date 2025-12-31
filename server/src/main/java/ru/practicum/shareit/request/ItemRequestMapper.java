package ru.practicum.shareit.request;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.item.ItemMapper;

@Mapper(componentModel = "spring",
        uses = {ItemMapper.class}, injectionStrategy = org.mapstruct.InjectionStrategy.CONSTRUCTOR)
public interface ItemRequestMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "description", source = "description")
    @Mapping(target = "items", ignore = true)
    @Mapping(target = "requester", ignore = true)
    @Mapping(target = "created", ignore = true)
    ItemRequestEntity toEntity(ItemRequestDto itemRequestDto);

    ItemRequestDto toResponseDto(ItemRequestEntity entity);
}
