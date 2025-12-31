package ru.practicum.shareit.request;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import ru.practicum.shareit.item.ItemEntity;
import ru.practicum.shareit.user.UserEntity;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "requests")
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItemRequestEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String description;

    @OneToMany(mappedBy = "request")
    private List<ItemEntity> items;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requester_id")
    private UserEntity requester;

    @CreationTimestamp
    private LocalDateTime created;
}
