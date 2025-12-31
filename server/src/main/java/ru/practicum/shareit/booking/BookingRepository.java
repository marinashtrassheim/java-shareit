package ru.practicum.shareit.booking;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<BookingEntity, Long>  {

    List<BookingEntity> findByBooker_Id(Long bookerId, Pageable pageable);

    @Query("SELECT b FROM BookingEntity b WHERE b.item.owner.id = :ownerId")
    List<BookingEntity> findByOwnerId(@Param("ownerId") Long ownerId, Pageable pageable);

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

    @Query("SELECT b FROM BookingEntity b " +
            "WHERE b.booker.id = :userId " +
            "AND b.startDate <= :now " +
            "AND b.endDate >= :now")
    List<BookingEntity> findCurrentBookings(@Param("userId") Long userId,
                                            @Param("now") LocalDateTime now,
                                            Pageable pageable);

    @Query("SELECT b FROM BookingEntity b " +
            "WHERE b.item.owner.id = :userId " +
            "AND b.startDate <= :now " +
            "AND b.endDate >= :now")
    List<BookingEntity> findCurrentOwnerBookings(@Param("userId") Long userId,
                                                @Param("now") LocalDateTime now,
                                                Pageable pageable);

    @Query("SELECT b FROM BookingEntity b " +
            "WHERE b.booker.id = :userId " +
            "AND b.endDate < :now")
    List<BookingEntity> findPastBookings(@Param("userId") Long userId,
                                         @Param("now") LocalDateTime now,
                                         Pageable pageable);

    @Query("SELECT b FROM BookingEntity b " +
            "WHERE b.item.owner.id = :userId " +
            "AND b.endDate < :now")
    List<BookingEntity> findPastOwnerBookings(@Param("userId") Long userId,
                                              @Param("now") LocalDateTime now,
                                              Pageable pageable);

    @Query("SELECT b FROM BookingEntity b " +
            "WHERE b.booker.id = :userId " +
            "AND b.startDate > :now")
    List<BookingEntity> findFutureBookings(@Param("userId") Long userId,
                                           @Param("now") LocalDateTime now,
                                           Pageable pageable);


    @Query("SELECT b FROM BookingEntity b " +
            "WHERE b.item.owner.id = :userId " +
            "AND b.startDate > :now")
    List<BookingEntity> findFutureOwnerBookings(@Param("userId") Long userId,
                                           @Param("now") LocalDateTime now,
                                           Pageable pageable);

    List<BookingEntity> findByBooker_IdAndStatus(Long bookerId, BookingStatus status, Pageable pageable);

    List<BookingEntity> findByItem_Owner_IdAndStatus(Long id, BookingStatus status, Pageable pageable);

    Optional<BookingEntity> findById(Long bookingId);
}
