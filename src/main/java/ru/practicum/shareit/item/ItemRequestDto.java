package ru.practicum.shareit.item;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ItemRequestDto {
    @NotNull(message = "Название не может быть пустым")
    private String name;

    @NotNull(message = "Описание не может быть пустым")
    private String description;

    @NotNull(message = "Необходимо заполнить поле доступность")
    private Boolean available;

    private Integer requestId;
}
