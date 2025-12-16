package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;



public interface BookingRepository extends JpaRepository<BookingEntity, Long>  {

    List<BookingEntity> getBookingEntitiesByBookerId(Long bookerId);

    @Query("SELECT b FROM BookingEntity b " +
            "JOIN ItemEntity i ON b.itemId = i.id " +
            "WHERE i.ownerId = :ownerId " +
            "ORDER BY b.startDate DESC")
    List<BookingEntity> findByOwnerId(@Param("ownerId") Long ownerId);

    BookingEntity getBookingEntityByItemIdAndBookerIdAndStatus(Long itemId, Long bookerId, BookingStatus status);

    @Query("SELECT b FROM BookingEntity b WHERE b.itemId = :itemId AND b.status = 'APPROVED' AND b.endDate < CURRENT_TIMESTAMP ORDER BY b.endDate DESC")
    List<BookingEntity> findLastBookings(@Param("itemId") Long itemId);

    @Query("SELECT b FROM BookingEntity b WHERE b.itemId = :itemId AND b.status = 'APPROVED' AND b.startDate > CURRENT_TIMESTAMP ORDER BY b.startDate ASC")
    List<BookingEntity> findNextBookings(@Param("itemId") Long itemId);
}
