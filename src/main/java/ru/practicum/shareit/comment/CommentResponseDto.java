package ru.practicum.shareit.comment;

import lombok.Builder;
import lombok.Data;


@Builder
@Data
public class CommentResponseDto {
    private Long id;
    private String text;
    private Long itemId;
    private Long authorId;
}
