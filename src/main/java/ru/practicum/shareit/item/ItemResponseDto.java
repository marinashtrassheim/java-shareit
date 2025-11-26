package ru.practicum.shareit.item;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ItemResponseDto {
    private int id;
    private String name;
    private String description;
    private Boolean available;
}
