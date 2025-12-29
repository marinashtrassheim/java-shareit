package ru.practicum.shareit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import ru.practicum.shareit.booking.BookingEntity;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.item.ItemEntity;
import ru.practicum.shareit.user.UserEntity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class BookingRepositoryIntegrationTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private BookingRepository bookingRepository;

    private UserEntity owner;
    private UserEntity booker;
    private ItemEntity item;
    private BookingEntity booking;

    @BeforeEach
    void setUp() {
        owner = UserEntity.builder()
                .name("Owner")
                .email("owner@example.com")
                .build();
        entityManager.persist(owner);

        booker = UserEntity.builder()
                .name("Booker")
                .email("booker@example.com")
                .build();
        entityManager.persist(booker);

        item = ItemEntity.builder()
                .name("Drill")
                .description("Power drill")
                .available(true)
                .owner(owner)
                .build();
        entityManager.persist(item);

        booking = BookingEntity.builder()
                .startDate(LocalDateTime.now().minusDays(2))
                .endDate(LocalDateTime.now().plusDays(2))
                .item(item)
                .booker(booker)
                .status(BookingStatus.APPROVED)
                .build();
        entityManager.persist(booking);
        entityManager.flush();
    }

    @Test
    void findByBookerId_shouldReturnBookingsForBooker() {
        Pageable pageable = PageRequest.of(0, 10);
        List<BookingEntity> result = bookingRepository.findByBooker_Id(booker.getId(), pageable);

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getId()).isEqualTo(booking.getId());
        assertThat(result.getFirst().getBooker().getId()).isEqualTo(booker.getId());
    }

    @Test
    void findByOwnerId_shouldReturnBookingsForOwner() {
        Pageable pageable = PageRequest.of(0, 10);
        List<BookingEntity> result = bookingRepository.findByOwnerId(owner.getId(), pageable);

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getId()).isEqualTo(booking.getId());
        assertThat(result.getFirst().getItem().getOwner().getId()).isEqualTo(owner.getId());
    }

    @Test
    void findByItem_IdAndBooker_IdAndStatus_shouldReturnBooking() {
        Optional<BookingEntity> result = bookingRepository.findByItem_IdAndBooker_IdAndStatus(
                item.getId(), booker.getId(), BookingStatus.APPROVED);

        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(booking.getId());
    }

    @Test
    void findFirstByItemIdAndEndDateBeforeOrderByEndDateDesc_shouldReturnLastBooking() {
        BookingEntity pastBooking = BookingEntity.builder()
                .startDate(LocalDateTime.now().minusDays(10))
                .endDate(LocalDateTime.now().minusDays(5))
                .item(item)
                .booker(booker)
                .status(BookingStatus.APPROVED)
                .build();
        entityManager.persist(pastBooking);
        entityManager.flush();

        Optional<BookingEntity> result = bookingRepository
                .findFirstByItemIdAndEndDateBeforeOrderByEndDateDesc(
                        item.getId(), LocalDateTime.now());

        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(pastBooking.getId());
    }

    @Test
    void findFirstByItemIdAndStartDateAfterOrderByStartDateAsc_shouldReturnNextBooking() {
        BookingEntity futureBooking = BookingEntity.builder()
                .startDate(LocalDateTime.now().plusDays(5))
                .endDate(LocalDateTime.now().plusDays(10))
                .item(item)
                .booker(booker)
                .status(BookingStatus.APPROVED)
                .build();
        entityManager.persist(futureBooking);
        entityManager.flush();

        Optional<BookingEntity> result = bookingRepository
                .findFirstByItemIdAndStartDateAfterOrderByStartDateAsc(
                        item.getId(), LocalDateTime.now());

        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(futureBooking.getId());
    }

    @Test
    void findByBooker_IdAndStatus_shouldReturnBookingsWithStatus() {
        BookingEntity waitingBooking = BookingEntity.builder()
                .startDate(LocalDateTime.now().plusDays(1))
                .endDate(LocalDateTime.now().plusDays(3))
                .item(item)
                .booker(booker)
                .status(BookingStatus.WAITING)
                .build();
        entityManager.persist(waitingBooking);
        entityManager.flush();

        Pageable pageable = PageRequest.of(0, 10);
        List<BookingEntity> approved = bookingRepository
                .findByBooker_IdAndStatus(booker.getId(), BookingStatus.APPROVED, pageable);
        List<BookingEntity> waiting = bookingRepository
                .findByBooker_IdAndStatus(booker.getId(), BookingStatus.WAITING, pageable);

        assertThat(approved).hasSize(1);
        assertThat(waiting).hasSize(1);
        assertThat(approved.getFirst().getStatus()).isEqualTo(BookingStatus.APPROVED);
        assertThat(waiting.getFirst().getStatus()).isEqualTo(BookingStatus.WAITING);
    }

    @Test
    void findByItem_Owner_IdAndStatus_shouldReturnOwnerBookingsWithStatus() {
        Pageable pageable = PageRequest.of(0, 10);
        List<BookingEntity> result = bookingRepository
                .findByItem_Owner_IdAndStatus(owner.getId(), BookingStatus.APPROVED, pageable);

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getItem().getOwner().getId()).isEqualTo(owner.getId());
        assertThat(result.getFirst().getStatus()).isEqualTo(BookingStatus.APPROVED);
    }
}
