package ru.practicum.shareit.user;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * TODO Sprint add-controllers.
 */
@Data
@EqualsAndHashCode(of = "email")
@Builder
public class User {
    private int id;
    private String name;
    private String email;
}
