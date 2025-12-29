package ru.practicum.shareit.comment;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<CommentEntity, Long>  {
    List<CommentEntity> findAllByItemId(Long itemId);
}
