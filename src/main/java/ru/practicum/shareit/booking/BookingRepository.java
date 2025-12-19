package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<BookingEntity, Long>  {

    List<BookingEntity> getBookingEntitiesByBookerId(Long bookerId);

    List<BookingEntity> findByBooker_Id(Long bookerId);

    @Query("SELECT b FROM BookingEntity b WHERE b.item.owner.id = :ownerId")
    List<BookingEntity> findByOwnerId(@Param("ownerId") Long ownerId);

    Optional<BookingEntity> getBookingEntityByItemIdAndBookerIdAndStatus(Long itemId, Long bookerId, BookingStatus status);

    Optional<BookingEntity> findByItem_IdAndBooker_IdAndStatus(
            Long itemId, Long bookerId, BookingStatus status);

    Optional<BookingEntity> findFirstByItemIdAndEndDateBeforeOrderByEndDateDesc(
            Long itemId,
            LocalDateTime endDate
    );

    Optional<BookingEntity> findFirstByItemIdAndStartDateAfterOrderByStartDateAsc(
            Long itemId,
            LocalDateTime startDate
    );


    Optional<BookingEntity> findFirstByItemIdAndStartDateBeforeAndEndDateAfter(
            Long itemId,
            LocalDateTime startDate,
            LocalDateTime endDate
    );

}
