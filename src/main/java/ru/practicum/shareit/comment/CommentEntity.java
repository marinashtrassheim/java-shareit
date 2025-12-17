package ru.practicum.shareit.comment;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "comments")
@Getter
@Setter
public class CommentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "text")
    private String text;

    @Column(name = "item_id")
    private Long itemId;

    @Column(name = "author_id")
    private Long authorId;

    @Column(name = "created")
    private LocalDateTime created;

}
