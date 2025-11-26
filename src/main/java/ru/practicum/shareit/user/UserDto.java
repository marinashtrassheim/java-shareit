package ru.practicum.shareit.user;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import ru.practicum.shareit.validation.ValidationGroups;

@Data
@EqualsAndHashCode(of = "email")
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserDto {
    private int id;
    @NotBlank(message = "Имя не может быть пустым", groups = ValidationGroups.OnCreate.class)
    private String name;
    @NotBlank(message = "Email не может быть пустым", groups = ValidationGroups.OnCreate.class)
    @Email(message = "Некорректный формат email", groups = ValidationGroups.OnCreate.class)
    private String email;
}
