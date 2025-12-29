package ru.practicum.shareit.user;

import jakarta.persistence.*;
import lombok.*;
import ru.practicum.shareit.booking.BookingEntity;
import ru.practicum.shareit.comment.CommentEntity;
import ru.practicum.shareit.item.ItemEntity;
import ru.practicum.shareit.request.ItemRequestEntity;

import java.util.List;

@Entity
@Table(name = "users")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @OneToMany(mappedBy = "booker")
    private List<BookingEntity> bookings;

    @OneToMany(mappedBy = "author")
    private List<CommentEntity> comments;

    @OneToMany(mappedBy = "requester")
    private List<ItemRequestEntity> requests;

    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemEntity> items;

}
