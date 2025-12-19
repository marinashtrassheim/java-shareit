package ru.practicum.shareit.request;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

/**
 * TODO Sprint add-item-requests.
 */
@Data
@EqualsAndHashCode(of = "id")
public class ItemRequest {
    private int id;
    private String description;
    private LocalDate created;
}
