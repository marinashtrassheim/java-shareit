package ru.practicum.shareit.booking;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.user.UserMapper;

@Mapper(componentModel = "spring", uses = {UserMapper.class, ItemMapper.class},
        injectionStrategy = org.mapstruct.InjectionStrategy.CONSTRUCTOR)
public interface BookingMapper {

    @Mapping(target = "start", source = "startDate")
    @Mapping(target = "end", source = "endDate")
    @Mapping(target = "booker", source = "booker")
    @Mapping(target = "item", source = "item")
    @Mapping(target = "itemId", ignore = true)
    BookingDto toDto(BookingEntity entity);

    @Mapping(target = "bookerId", source = "booker.id")  // Entity.booker.id → ShortDto.bookerId
    BookingShortDto toShortDto(BookingEntity entity);

    @Mapping(target = "startDate", source = "start")
    @Mapping(target = "endDate", source = "end")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "item", ignore = true)
    @Mapping(target = "booker", ignore = true)
    @Mapping(target = "status", ignore = true)
    BookingEntity toEntity(BookingDto dto);

}
