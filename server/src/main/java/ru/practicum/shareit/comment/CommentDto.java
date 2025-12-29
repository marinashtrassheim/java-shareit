package ru.practicum.shareit.comment;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class CommentDto {
    private Long id;
    private String text;
    private Long itemId;
    private Long authorId;
    private String authorName;
    private LocalDateTime created;
}