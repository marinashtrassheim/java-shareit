package ru.practicum.shareit.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import ru.practicum.shareit.user.User;

/**
 * TODO Sprint add-item-requests.
 */
@Data
@EqualsAndHashCode(of = "id")
public class ItemRequestDto {
    private Integer id;
    private String description;
    @NotNull(message = "Пользователь обязателен для указания")
    private User requester;

}
