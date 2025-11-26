package ru.practicum.shareit.item;

import ru.practicum.shareit.user.User;

public class ItemMapper {

    public static Item toItemFromRequest(ItemRequestDto requestDto, User user) {
        return Item.builder()
                .name(requestDto.getName())
                .description(requestDto.getDescription())
                .available(requestDto.getAvailable())
                .user(user)
                .request(null)
                .build();
    }

    public static ItemResponseDto toResponseDto(Item item) {
        return ItemResponseDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .build();
    }
}
