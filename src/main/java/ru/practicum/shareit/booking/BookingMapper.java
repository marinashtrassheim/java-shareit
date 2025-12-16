package ru.practicum.shareit.booking;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.user.UserMapper;

@Mapper(componentModel = "spring", uses = {UserMapper.class, ItemMapper.class})
public interface BookingMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "item", ignore = true)
    @Mapping(target = "booker", ignore = true)
    @Mapping(target = "status", constant = "WAITING")
    Booking toBooking(BookingRequestDto requestDto);

    @Mapping(target = "itemId", source = "item.id")
    @Mapping(target = "bookerId", source = "booker.id")
    BookingEntity toEntity(Booking booking);


    @Mapping(target = "id", source = "id")
    @Mapping(target = "start", source = "startDate")
    @Mapping(target = "end", source = "endDate")
    @Mapping(target = "status", source = "status")
    @Mapping(target = "item", ignore = true)
    @Mapping(target = "booker", ignore = true)
    Booking toBooking(BookingEntity entity);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "start", source = "start")
    @Mapping(target = "end", source = "end")
    @Mapping(target = "status", source = "status")
    @Mapping(target = "booker", source = "booker")
    @Mapping(target = "item", source = "item")
    BookingResponseDto toResponseDto(Booking booking);

    @Mapping(target = "bookerId", source = "bookerId")
    BookingShortDto toBookingShortDto(BookingEntity bookingEntity);
}
