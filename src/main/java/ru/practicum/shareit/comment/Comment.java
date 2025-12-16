package ru.practicum.shareit.comment;

import lombok.Data;
import lombok.EqualsAndHashCode;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;

@Data
@EqualsAndHashCode(of = "id")
public class Comment {
    private Long id;
    private String text;
    private Item item;
    private User author;
}
