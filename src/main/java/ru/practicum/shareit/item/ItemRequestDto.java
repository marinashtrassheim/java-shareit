package ru.practicum.shareit.item;


import lombok.Data;

@Data
public class ItemRequestDto {
    private String name;
    private String description;
    private Boolean available;
    private Integer requestId;
}
