package ru.practicum.shareit.item.dto;


import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ItemRequestDto {
    private String name;
    private String description;

    @NotNull(message = "Поле должно быть заполнено")
    private Boolean available;
    private Long requestId;
}
