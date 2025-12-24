package ru.practicum.shareit.item;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;


public interface ItemRepository extends JpaRepository<ItemEntity, Long> {

    List<ItemEntity> findByOwnerId(Long ownerId);

    @Query("SELECT i FROM ItemEntity i " +
            "WHERE i.available = true " +
            "AND (LOWER(i.name) LIKE LOWER(CONCAT('%', :text, '%')) " +
            "OR LOWER(i.description) LIKE LOWER(CONCAT('%', :text, '%')))")
    List<ItemEntity> searchAvailableItems(@Param("text") String text);

    @Query("SELECT COUNT(i) > 0 FROM ItemEntity i WHERE i.id = :itemId AND i.owner.id = :ownerId")
    boolean existsByIdAndOwnerId(@Param("itemId") Long itemId,
                                 @Param("ownerId") Long ownerId);
}


