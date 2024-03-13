package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingReadDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.enums.Status;
import ru.practicum.shareit.exception.InvalidStatusException;
import ru.practicum.shareit.exception.NotAvailableException;
import ru.practicum.shareit.exception.NotBelongToUser;
import ru.practicum.shareit.exception.NotOwnedException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@Transactional
@SpringBootTest(
        properties = "db.name = test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE
)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class BookingServiceImplTest {
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final BookingService bookingService;
    private final EntityManager em;
    private User user1;
    private User user2;
    private User user3;
    private Item item1;
    private Item item2;
    private Item item3;

    @BeforeEach
    void setUp() {
        User user1 = User.builder()
                .name("John")
                .email("first@mail.com")
                .build();
        User user2 = User.builder()
                .name("Max")
                .email("second@mail.com")
                .build();
        User user3 = User.builder()
                .name("Ivan")
                .email("third@mail.com")
                .build();

        this.user1 = userRepository.save(user1);
        this.user2 = userRepository.save(user2);
        this.user3 = userRepository.save(user3);

        Item item1 = Item.builder()
                .name("item1")
                .description("descr1")
                .available(true)
                .owner(this.user1).build();

        Item item2 = Item.builder()
                .name("item2")
                .description("descr2")
                .available(true)
                .owner(this.user2).build();

        Item item4 = Item.builder()
                .name("item3")
                .description("descr3")
                .available(true)
                .owner(this.user1).build();

        this.item1 = itemRepository.save(item1);
        this.item2 = itemRepository.save(item2);
        this.item3 = itemRepository.save(item4);
    }

    @Test
    @DisplayName("Создание booking")
    void saveBooking() {
        BookingCreateDto bookingDto = makeBookingCreateDto(LocalDateTime.now().plus(Duration.ofDays(1)),
                LocalDateTime.now().plus(Duration.ofDays(2)), item1.getId());

        BookingReadDto saveBookingDto = bookingService.saveBooking(user2.getId(), bookingDto);

        TypedQuery<Booking> query = em.createQuery("Select b from Booking b where b.id = :id", Booking.class);
        Booking booking = query.setParameter("id", saveBookingDto.getId())
                .getSingleResult();

        assertThat(booking.getId(), notNullValue());
        assertThat(booking.getStart(), equalTo(bookingDto.getStart()));
        assertThat(booking.getEnd(), equalTo(bookingDto.getEnd()));
        assertThat(booking.getItem().getId(), equalTo(bookingDto.getItemId()));
        assertThat(booking.getStatus(), equalTo(Status.WAITING));
    }

    @Test
    @DisplayName("Создание booking владельцем item")
    void saveOwnerSaveBooking() {
        BookingCreateDto bookingDto = makeBookingCreateDto(LocalDateTime.now().plus(Duration.ofDays(1)),
                LocalDateTime.now().plus(Duration.ofDays(2)), item1.getId());

        assertThatThrownBy(() -> bookingService.saveBooking(user1.getId(), bookingDto))
                .isInstanceOf(NotOwnedException.class)
                .hasMessageContaining("Item id: " + item1.getId() + " belong to user id: " + user1.getId());
    }

    @Test
    @DisplayName("Создание booking для недоступной item")
    void saveBookingForNotAvailableItem() {
        item2.setAvailable(false);
        itemRepository.save(item2);
        BookingCreateDto bookingDto = makeBookingCreateDto(LocalDateTime.now().plus(Duration.ofDays(1)),
                LocalDateTime.now().plus(Duration.ofDays(2)), item2.getId());

        assertThatThrownBy(() -> bookingService.saveBooking(user1.getId(), bookingDto))
                .isInstanceOf(NotAvailableException.class)
                .hasMessageContaining("Item not available with id: " + item2.getId());
    }

    @Test
    @DisplayName("Подтверждение статуса бронирования на approved")
    void approvedBookingToApproved() {
        BookingCreateDto bookingDto = makeBookingCreateDto(LocalDateTime.now().plus(Duration.ofDays(1)),
                LocalDateTime.now().plus(Duration.ofDays(2)), item1.getId());
        BookingReadDto saveBookingDto = bookingService.saveBooking(user2.getId(), bookingDto);
        bookingService.approvedBooking(user1.getId(), saveBookingDto.getId(), true);

        TypedQuery<Booking> query = em.createQuery("Select b from Booking b where b.id = :id", Booking.class);
        Booking booking = query.setParameter("id", saveBookingDto.getId())
                .getSingleResult();

        assertThat(booking.getId(), notNullValue());
        assertThat(booking.getStart(), equalTo(bookingDto.getStart()));
        assertThat(booking.getEnd(), equalTo(bookingDto.getEnd()));
        assertThat(booking.getItem().getId(), equalTo(bookingDto.getItemId()));
        assertThat(booking.getStatus(), equalTo(Status.APPROVED));
    }

    @Test
    @DisplayName("Подтверждение статуса бронирования на rejected")
    void approvedBookingToRejected() {
        BookingCreateDto bookingDto = makeBookingCreateDto(LocalDateTime.now().plus(Duration.ofDays(1)),
                LocalDateTime.now().plus(Duration.ofDays(2)), item1.getId());
        BookingReadDto saveBookingDto = bookingService.saveBooking(user2.getId(), bookingDto);
        bookingService.approvedBooking(user1.getId(), saveBookingDto.getId(), false);

        TypedQuery<Booking> query = em.createQuery("Select b from Booking b where b.id = :id", Booking.class);
        Booking booking = query.setParameter("id", saveBookingDto.getId())
                .getSingleResult();

        assertThat(booking.getId(), notNullValue());
        assertThat(booking.getStart(), equalTo(bookingDto.getStart()));
        assertThat(booking.getEnd(), equalTo(bookingDto.getEnd()));
        assertThat(booking.getItem().getId(), equalTo(bookingDto.getItemId()));
        assertThat(booking.getStatus(), equalTo(Status.REJECTED));
    }

    @Test
    @DisplayName("Подтверждение статуса бронирования другим User")
    void approvedBookingAndThrowNotOwnedException() {
        BookingCreateDto bookingDto = makeBookingCreateDto(LocalDateTime.now().plus(Duration.ofDays(1)),
                LocalDateTime.now().plus(Duration.ofDays(2)), item1.getId());
        BookingReadDto saveBookingDto = bookingService.saveBooking(user2.getId(), bookingDto);

        assertThatThrownBy(() -> bookingService.approvedBooking(user3.getId(), saveBookingDto.getId(), false))
                .isInstanceOf(NotOwnedException.class)
                .hasMessageContaining("Item id = " + item1.getId() +
                        " does not belong to user userId = " + user3.getId());
    }

    @Test
    @DisplayName("Подтверждение статуса бронирования для недоступной вещи")
    void approvedBookingForNotAvailableItemAndThrowNotAvailableException() {
        BookingCreateDto bookingDto = makeBookingCreateDto(LocalDateTime.now().plus(Duration.ofDays(1)),
                LocalDateTime.now().plus(Duration.ofDays(2)), item1.getId());
        BookingReadDto saveBookingDto = bookingService.saveBooking(user2.getId(), bookingDto);
        item1.setAvailable(false);
        itemRepository.save(item1);

        assertThatThrownBy(() -> bookingService.approvedBooking(user1.getId(), saveBookingDto.getId(), false))
                .isInstanceOf(NotAvailableException.class)
                .hasMessageContaining("Item not available with id: " + item1.getId());
    }

    @Test
    @DisplayName("Подтверждение статуса бронирования для booking с некоректным статусом")
    void approvedBookingWithIncorrectStatusAndThrowInvalidStatusException() {
        BookingCreateDto bookingDto = makeBookingCreateDto(LocalDateTime.now().plus(Duration.ofDays(1)),
                LocalDateTime.now().plus(Duration.ofDays(2)), item1.getId());
        BookingReadDto saveBookingDto = bookingService.saveBooking(user2.getId(), bookingDto);
        bookingService.approvedBooking(user1.getId(), saveBookingDto.getId(), false);

        assertThatThrownBy(() -> bookingService.approvedBooking(user1.getId(), saveBookingDto.getId(), false))
                .isInstanceOf(InvalidStatusException.class)
                .hasMessageContaining("Booking status is not waiting, cannot approve or reject");
    }

    @Test
    @DisplayName("Получение статуса booking для владельца item")
    void getStatusForOwnerItem() {
        BookingCreateDto bookingDto = makeBookingCreateDto(LocalDateTime.now().plus(Duration.ofDays(1)),
                LocalDateTime.now().plus(Duration.ofDays(2)), item1.getId());
        BookingReadDto saveBookingDto = bookingService.saveBooking(user2.getId(), bookingDto);
        bookingService.approvedBooking(user1.getId(), saveBookingDto.getId(), false);

        BookingReadDto booking = bookingService.getStatus(user1.getId(), saveBookingDto.getId());

        assertThat(booking.getId(), notNullValue());
        assertThat(booking.getStart(), equalTo(bookingDto.getStart()));
        assertThat(booking.getEnd(), equalTo(bookingDto.getEnd()));
        assertThat(booking.getItem().getId(), equalTo(bookingDto.getItemId()));
        assertThat(booking.getStatus(), equalTo(Status.REJECTED));
    }

    @Test
    @DisplayName("Получение статуса booking для бронирующего")
    void getStatusForBooker() {
        BookingCreateDto bookingDto = makeBookingCreateDto(LocalDateTime.now().plus(Duration.ofDays(1)),
                LocalDateTime.now().plus(Duration.ofDays(2)), item1.getId());
        BookingReadDto saveBookingDto = bookingService.saveBooking(user2.getId(), bookingDto);
        bookingService.approvedBooking(user1.getId(), saveBookingDto.getId(), false);

        BookingReadDto booking = bookingService.getStatus(user2.getId(), saveBookingDto.getId());

        assertThat(booking.getId(), notNullValue());
        assertThat(booking.getStart(), equalTo(bookingDto.getStart()));
        assertThat(booking.getEnd(), equalTo(bookingDto.getEnd()));
        assertThat(booking.getItem().getId(), equalTo(bookingDto.getItemId()));
        assertThat(booking.getStatus(), equalTo(Status.REJECTED));
    }

    @Test
    @DisplayName("Получение статуса booking для случайного user")
    void getStatusForOtherUserAndThrowNotBelongToUser() {
        BookingCreateDto bookingDto = makeBookingCreateDto(LocalDateTime.now().plus(Duration.ofDays(1)),
                LocalDateTime.now().plus(Duration.ofDays(2)), item1.getId());
        BookingReadDto saveBookingDto = bookingService.saveBooking(user2.getId(), bookingDto);
        bookingService.approvedBooking(user1.getId(), saveBookingDto.getId(), false);

        assertThatThrownBy(() -> bookingService.getStatus(user3.getId(), saveBookingDto.getId()))
                .isInstanceOf(NotBelongToUser.class)
                .hasMessageContaining("Booking not belong to users id: " + user3.getId());
    }

    @Test
    @DisplayName("Получние текущих booking для booker")
    void getAllCurrentBookingsForBooker() {
        BookingCreateDto bookingDto1 = makeBookingCreateDto(LocalDateTime.now().minus(Duration.ofDays(2)),
                LocalDateTime.now().plus(Duration.ofDays(2)), item1.getId());
        BookingCreateDto bookingDto2 = makeBookingCreateDto(LocalDateTime.now().minus(Duration.ofDays(1)),
                LocalDateTime.now().plus(Duration.ofDays(2)), item2.getId());
        BookingReadDto saveBookingDto1 = bookingService.saveBooking(user3.getId(), bookingDto1);
        BookingReadDto saveBookingDto2 = bookingService.saveBooking(user3.getId(), bookingDto2);

        List<BookingReadDto> allBookingsForBooker = bookingService.getAllBookingsForBooker(user3.getId(),
                Status.CURRENT,
                0,
                10);

        BookingReadDto booking1 = allBookingsForBooker.get(0);
        assertThat(booking1.getId(), equalTo(saveBookingDto2.getId()));
        assertThat(booking1.getStart(), equalTo(saveBookingDto2.getStart()));
        assertThat(booking1.getEnd(), equalTo(saveBookingDto2.getEnd()));
        assertThat(booking1.getItem().getId(), equalTo(saveBookingDto2.getItem().getId()));
        assertThat(booking1.getStatus(), equalTo(saveBookingDto2.getStatus()));

        BookingReadDto booking2 = allBookingsForBooker.get(1);
        assertThat(booking2.getId(), equalTo(saveBookingDto1.getId()));
        assertThat(booking2.getStart(), equalTo(saveBookingDto1.getStart()));
        assertThat(booking2.getEnd(), equalTo(saveBookingDto1.getEnd()));
        assertThat(booking2.getItem().getId(), equalTo(saveBookingDto1.getItem().getId()));
        assertThat(booking2.getStatus(), equalTo(saveBookingDto1.getStatus()));
    }

    @Test
    @DisplayName("Получние прошедших booking для booker")
    void getAllPastBookingsForBooker() {
        BookingCreateDto bookingDto1 = makeBookingCreateDto(LocalDateTime.now().minus(Duration.ofDays(2)),
                LocalDateTime.now().minus(Duration.ofHours(2)), item1.getId());
        BookingCreateDto bookingDto2 = makeBookingCreateDto(LocalDateTime.now().minus(Duration.ofDays(1)),
                LocalDateTime.now().minus(Duration.ofHours(2)), item2.getId());
        BookingReadDto saveBookingDto1 = bookingService.saveBooking(user3.getId(), bookingDto1);
        BookingReadDto saveBookingDto2 = bookingService.saveBooking(user3.getId(), bookingDto2);

        List<BookingReadDto> allBookingsForBooker = bookingService.getAllBookingsForBooker(user3.getId(),
                Status.PAST,
                0,
                10);

        BookingReadDto booking1 = allBookingsForBooker.get(0);
        assertThat(booking1.getId(), equalTo(saveBookingDto2.getId()));
        assertThat(booking1.getStart(), equalTo(saveBookingDto2.getStart()));
        assertThat(booking1.getEnd(), equalTo(saveBookingDto2.getEnd()));
        assertThat(booking1.getItem().getId(), equalTo(saveBookingDto2.getItem().getId()));
        assertThat(booking1.getStatus(), equalTo(saveBookingDto2.getStatus()));

        BookingReadDto booking2 = allBookingsForBooker.get(1);
        assertThat(booking2.getId(), equalTo(saveBookingDto1.getId()));
        assertThat(booking2.getStart(), equalTo(saveBookingDto1.getStart()));
        assertThat(booking2.getEnd(), equalTo(saveBookingDto1.getEnd()));
        assertThat(booking2.getItem().getId(), equalTo(saveBookingDto1.getItem().getId()));
        assertThat(booking2.getStatus(), equalTo(saveBookingDto1.getStatus()));
    }

    @Test
    @DisplayName("Получение будущих booking для booker")
    void getAllFutureBookingsForBooker() {
        BookingCreateDto bookingDto1 = makeBookingCreateDto(LocalDateTime.now().plus(Duration.ofDays(1)),
                LocalDateTime.now().plus(Duration.ofDays(2)), item1.getId());
        BookingCreateDto bookingDto2 = makeBookingCreateDto(LocalDateTime.now().plus(Duration.ofDays(3)),
                LocalDateTime.now().plus(Duration.ofDays(4)), item2.getId());
        BookingReadDto saveBookingDto1 = bookingService.saveBooking(user3.getId(), bookingDto1);
        BookingReadDto saveBookingDto2 = bookingService.saveBooking(user3.getId(), bookingDto2);

        List<BookingReadDto> allBookingsForBooker = bookingService.getAllBookingsForBooker(user3.getId(),
                Status.FUTURE,
                0,
                10);

        BookingReadDto booking1 = allBookingsForBooker.get(0);
        assertThat(booking1.getId(), equalTo(saveBookingDto2.getId()));
        assertThat(booking1.getStart(), equalTo(saveBookingDto2.getStart()));
        assertThat(booking1.getEnd(), equalTo(saveBookingDto2.getEnd()));
        assertThat(booking1.getItem().getId(), equalTo(saveBookingDto2.getItem().getId()));
        assertThat(booking1.getStatus(), equalTo(saveBookingDto2.getStatus()));

        BookingReadDto booking2 = allBookingsForBooker.get(1);
        assertThat(booking2.getId(), equalTo(saveBookingDto1.getId()));
        assertThat(booking2.getStart(), equalTo(saveBookingDto1.getStart()));
        assertThat(booking2.getEnd(), equalTo(saveBookingDto1.getEnd()));
        assertThat(booking2.getItem().getId(), equalTo(saveBookingDto1.getItem().getId()));
        assertThat(booking2.getStatus(), equalTo(saveBookingDto1.getStatus()));
    }

    @Test
    @DisplayName("Получние всех booking для booker")
    void getAllBookingsForBooker() {
        BookingCreateDto bookingDto1 = makeBookingCreateDto(LocalDateTime.now().minus(Duration.ofDays(2)),
                LocalDateTime.now().minus(Duration.ofDays(1)), item1.getId());
        BookingCreateDto bookingDto2 = makeBookingCreateDto(LocalDateTime.now().plus(Duration.ofDays(3)),
                LocalDateTime.now().plus(Duration.ofDays(4)), item2.getId());
        BookingReadDto saveBookingDto1 = bookingService.saveBooking(user3.getId(), bookingDto1);
        BookingReadDto saveBookingDto2 = bookingService.saveBooking(user3.getId(), bookingDto2);

        List<BookingReadDto> allBookingsForBooker = bookingService.getAllBookingsForBooker(user3.getId(),
                Status.ALL,
                0,
                10);

        BookingReadDto booking1 = allBookingsForBooker.get(0);
        assertThat(booking1.getId(), equalTo(saveBookingDto2.getId()));
        assertThat(booking1.getStart(), equalTo(saveBookingDto2.getStart()));
        assertThat(booking1.getEnd(), equalTo(saveBookingDto2.getEnd()));
        assertThat(booking1.getItem().getId(), equalTo(saveBookingDto2.getItem().getId()));
        assertThat(booking1.getStatus(), equalTo(saveBookingDto2.getStatus()));

        BookingReadDto booking2 = allBookingsForBooker.get(1);
        assertThat(booking2.getId(), equalTo(saveBookingDto1.getId()));
        assertThat(booking2.getStart(), equalTo(saveBookingDto1.getStart()));
        assertThat(booking2.getEnd(), equalTo(saveBookingDto1.getEnd()));
        assertThat(booking2.getItem().getId(), equalTo(saveBookingDto1.getItem().getId()));
        assertThat(booking2.getStatus(), equalTo(saveBookingDto1.getStatus()));
    }

    @Test
    @DisplayName("Получние booking со статусом waiting для booker")
    void getAllWaitingBookingsForBooker() {
        BookingCreateDto bookingDto1 = makeBookingCreateDto(LocalDateTime.now().minus(Duration.ofDays(2)),
                LocalDateTime.now().minus(Duration.ofDays(1)), item1.getId());
        BookingCreateDto bookingDto2 = makeBookingCreateDto(LocalDateTime.now().plus(Duration.ofDays(3)),
                LocalDateTime.now().plus(Duration.ofDays(4)), item2.getId());
        BookingReadDto saveBookingDto1 = bookingService.saveBooking(user3.getId(), bookingDto1);
        BookingReadDto saveBookingDto2 = bookingService.saveBooking(user3.getId(), bookingDto2);

        List<BookingReadDto> allBookingsForBooker = bookingService.getAllBookingsForBooker(user3.getId(),
                Status.WAITING,
                0,
                10);

        BookingReadDto booking1 = allBookingsForBooker.get(0);
        assertThat(booking1.getId(), equalTo(saveBookingDto2.getId()));
        assertThat(booking1.getStart(), equalTo(saveBookingDto2.getStart()));
        assertThat(booking1.getEnd(), equalTo(saveBookingDto2.getEnd()));
        assertThat(booking1.getItem().getId(), equalTo(saveBookingDto2.getItem().getId()));
        assertThat(booking1.getStatus(), equalTo(saveBookingDto2.getStatus()));

        BookingReadDto booking2 = allBookingsForBooker.get(1);
        assertThat(booking2.getId(), equalTo(saveBookingDto1.getId()));
        assertThat(booking2.getStart(), equalTo(saveBookingDto1.getStart()));
        assertThat(booking2.getEnd(), equalTo(saveBookingDto1.getEnd()));
        assertThat(booking2.getItem().getId(), equalTo(saveBookingDto1.getItem().getId()));
        assertThat(booking2.getStatus(), equalTo(saveBookingDto1.getStatus()));
    }

    @Test
    @DisplayName("Получние всех booking со статусом rejected для booker")
    void getAllRejectedBookingsForBooker() {
        BookingCreateDto bookingDto1 = makeBookingCreateDto(LocalDateTime.now().minus(Duration.ofDays(2)),
                LocalDateTime.now().minus(Duration.ofDays(1)), item1.getId());
        BookingCreateDto bookingDto2 = makeBookingCreateDto(LocalDateTime.now().plus(Duration.ofDays(3)),
                LocalDateTime.now().plus(Duration.ofDays(4)), item2.getId());
        BookingReadDto saveBookingDto1 = bookingService.saveBooking(user3.getId(), bookingDto1);
        BookingReadDto saveBookingDto2 = bookingService.saveBooking(user3.getId(), bookingDto2);
        bookingService.approvedBooking(user1.getId(), saveBookingDto1.getId(), false);
        bookingService.approvedBooking(user2.getId(), saveBookingDto2.getId(), false);

        List<BookingReadDto> allBookingsForBooker = bookingService.getAllBookingsForBooker(user3.getId(),
                Status.REJECTED,
                0,
                10);

        BookingReadDto booking1 = allBookingsForBooker.get(0);
        assertThat(booking1.getId(), equalTo(saveBookingDto2.getId()));
        assertThat(booking1.getStart(), equalTo(saveBookingDto2.getStart()));
        assertThat(booking1.getEnd(), equalTo(saveBookingDto2.getEnd()));
        assertThat(booking1.getItem().getId(), equalTo(saveBookingDto2.getItem().getId()));
        assertThat(booking1.getStatus(), equalTo(Status.REJECTED));

        BookingReadDto booking2 = allBookingsForBooker.get(1);
        assertThat(booking2.getId(), equalTo(saveBookingDto1.getId()));
        assertThat(booking2.getStart(), equalTo(saveBookingDto1.getStart()));
        assertThat(booking2.getEnd(), equalTo(saveBookingDto1.getEnd()));
        assertThat(booking2.getItem().getId(), equalTo(saveBookingDto1.getItem().getId()));
        assertThat(booking2.getStatus(), equalTo(Status.REJECTED));
    }

    @Test
    void getBookingItem() {
    }

    @Test
    @DisplayName("Получние текущих booking для owner")
    void getAllCurrentBookingsForOwner() {
        BookingCreateDto bookingDto1 = makeBookingCreateDto(LocalDateTime.now().minus(Duration.ofDays(2)),
                LocalDateTime.now().plus(Duration.ofDays(2)), item1.getId());
        BookingCreateDto bookingDto2 = makeBookingCreateDto(LocalDateTime.now().minus(Duration.ofDays(1)),
                LocalDateTime.now().plus(Duration.ofDays(2)), item3.getId());
        BookingReadDto saveBookingDto1 = bookingService.saveBooking(user3.getId(), bookingDto1);
        BookingReadDto saveBookingDto2 = bookingService.saveBooking(user3.getId(), bookingDto2);

        List<BookingReadDto> allBookingsForBooker = bookingService.getBookingsForOwnerItem(user1.getId(),
                Status.CURRENT,
                0,
                10);

        BookingReadDto booking1 = allBookingsForBooker.get(0);
        assertThat(booking1.getId(), equalTo(saveBookingDto2.getId()));
        assertThat(booking1.getStart(), equalTo(saveBookingDto2.getStart()));
        assertThat(booking1.getEnd(), equalTo(saveBookingDto2.getEnd()));
        assertThat(booking1.getItem().getId(), equalTo(saveBookingDto2.getItem().getId()));
        assertThat(booking1.getStatus(), equalTo(saveBookingDto2.getStatus()));

        BookingReadDto booking2 = allBookingsForBooker.get(1);
        assertThat(booking2.getId(), equalTo(saveBookingDto1.getId()));
        assertThat(booking2.getStart(), equalTo(saveBookingDto1.getStart()));
        assertThat(booking2.getEnd(), equalTo(saveBookingDto1.getEnd()));
        assertThat(booking2.getItem().getId(), equalTo(saveBookingDto1.getItem().getId()));
        assertThat(booking2.getStatus(), equalTo(saveBookingDto1.getStatus()));
    }

    @Test
    @DisplayName("Получние прошедших booking для owner")
    void getAllPastBookingsForOwner() {
        BookingCreateDto bookingDto1 = makeBookingCreateDto(LocalDateTime.now().minus(Duration.ofDays(2)),
                LocalDateTime.now().minus(Duration.ofHours(2)), item1.getId());
        BookingCreateDto bookingDto2 = makeBookingCreateDto(LocalDateTime.now().minus(Duration.ofDays(1)),
                LocalDateTime.now().minus(Duration.ofHours(2)), item3.getId());
        BookingReadDto saveBookingDto1 = bookingService.saveBooking(user3.getId(), bookingDto1);
        BookingReadDto saveBookingDto2 = bookingService.saveBooking(user3.getId(), bookingDto2);

        List<BookingReadDto> allBookingsForBooker = bookingService.getBookingsForOwnerItem(user1.getId(),
                Status.PAST,
                0,
                10);

        BookingReadDto booking1 = allBookingsForBooker.get(0);
        assertThat(booking1.getId(), equalTo(saveBookingDto2.getId()));
        assertThat(booking1.getStart(), equalTo(saveBookingDto2.getStart()));
        assertThat(booking1.getEnd(), equalTo(saveBookingDto2.getEnd()));
        assertThat(booking1.getItem().getId(), equalTo(saveBookingDto2.getItem().getId()));
        assertThat(booking1.getStatus(), equalTo(saveBookingDto2.getStatus()));

        BookingReadDto booking2 = allBookingsForBooker.get(1);
        assertThat(booking2.getId(), equalTo(saveBookingDto1.getId()));
        assertThat(booking2.getStart(), equalTo(saveBookingDto1.getStart()));
        assertThat(booking2.getEnd(), equalTo(saveBookingDto1.getEnd()));
        assertThat(booking2.getItem().getId(), equalTo(saveBookingDto1.getItem().getId()));
        assertThat(booking2.getStatus(), equalTo(saveBookingDto1.getStatus()));
    }

    @Test
    @DisplayName("Получение будущих booking для owner")
    void getAllFutureBookingsForOwner() {
        BookingCreateDto bookingDto1 = makeBookingCreateDto(LocalDateTime.now().plus(Duration.ofDays(1)),
                LocalDateTime.now().plus(Duration.ofDays(2)), item1.getId());
        BookingCreateDto bookingDto2 = makeBookingCreateDto(LocalDateTime.now().plus(Duration.ofDays(3)),
                LocalDateTime.now().plus(Duration.ofDays(4)), item3.getId());
        BookingReadDto saveBookingDto1 = bookingService.saveBooking(user3.getId(), bookingDto1);
        BookingReadDto saveBookingDto2 = bookingService.saveBooking(user3.getId(), bookingDto2);

        List<BookingReadDto> allBookingsForBooker = bookingService.getBookingsForOwnerItem(user1.getId(),
                Status.FUTURE,
                0,
                10);

        BookingReadDto booking1 = allBookingsForBooker.get(0);
        assertThat(booking1.getId(), equalTo(saveBookingDto2.getId()));
        assertThat(booking1.getStart(), equalTo(saveBookingDto2.getStart()));
        assertThat(booking1.getEnd(), equalTo(saveBookingDto2.getEnd()));
        assertThat(booking1.getItem().getId(), equalTo(saveBookingDto2.getItem().getId()));
        assertThat(booking1.getStatus(), equalTo(saveBookingDto2.getStatus()));

        BookingReadDto booking2 = allBookingsForBooker.get(1);
        assertThat(booking2.getId(), equalTo(saveBookingDto1.getId()));
        assertThat(booking2.getStart(), equalTo(saveBookingDto1.getStart()));
        assertThat(booking2.getEnd(), equalTo(saveBookingDto1.getEnd()));
        assertThat(booking2.getItem().getId(), equalTo(saveBookingDto1.getItem().getId()));
        assertThat(booking2.getStatus(), equalTo(saveBookingDto1.getStatus()));
    }

    @Test
    @DisplayName("Получние всех booking для owner")
    void getAllBookingsForOwner() {
        BookingCreateDto bookingDto1 = makeBookingCreateDto(LocalDateTime.now().minus(Duration.ofDays(2)),
                LocalDateTime.now().minus(Duration.ofDays(1)), item1.getId());
        BookingCreateDto bookingDto2 = makeBookingCreateDto(LocalDateTime.now().plus(Duration.ofDays(3)),
                LocalDateTime.now().plus(Duration.ofDays(4)), item3.getId());
        BookingReadDto saveBookingDto1 = bookingService.saveBooking(user3.getId(), bookingDto1);
        BookingReadDto saveBookingDto2 = bookingService.saveBooking(user3.getId(), bookingDto2);

        List<BookingReadDto> allBookingsForBooker = bookingService.getBookingsForOwnerItem(user1.getId(),
                Status.ALL,
                0,
                10);

        BookingReadDto booking1 = allBookingsForBooker.get(0);
        assertThat(booking1.getId(), equalTo(saveBookingDto2.getId()));
        assertThat(booking1.getStart(), equalTo(saveBookingDto2.getStart()));
        assertThat(booking1.getEnd(), equalTo(saveBookingDto2.getEnd()));
        assertThat(booking1.getItem().getId(), equalTo(saveBookingDto2.getItem().getId()));
        assertThat(booking1.getStatus(), equalTo(saveBookingDto2.getStatus()));

        BookingReadDto booking2 = allBookingsForBooker.get(1);
        assertThat(booking2.getId(), equalTo(saveBookingDto1.getId()));
        assertThat(booking2.getStart(), equalTo(saveBookingDto1.getStart()));
        assertThat(booking2.getEnd(), equalTo(saveBookingDto1.getEnd()));
        assertThat(booking2.getItem().getId(), equalTo(saveBookingDto1.getItem().getId()));
        assertThat(booking2.getStatus(), equalTo(saveBookingDto1.getStatus()));
    }

    @Test
    @DisplayName("Получние booking со статусом waiting для owner")
    void getAllWaitingBookingsForOwner() {
        BookingCreateDto bookingDto1 = makeBookingCreateDto(LocalDateTime.now().minus(Duration.ofDays(2)),
                LocalDateTime.now().minus(Duration.ofDays(1)), item1.getId());
        BookingCreateDto bookingDto2 = makeBookingCreateDto(LocalDateTime.now().plus(Duration.ofDays(3)),
                LocalDateTime.now().plus(Duration.ofDays(4)), item3.getId());
        BookingReadDto saveBookingDto1 = bookingService.saveBooking(user3.getId(), bookingDto1);
        BookingReadDto saveBookingDto2 = bookingService.saveBooking(user3.getId(), bookingDto2);

        List<BookingReadDto> allBookingsForBooker = bookingService.getBookingsForOwnerItem(user1.getId(),
                Status.WAITING,
                0,
                10);

        BookingReadDto booking1 = allBookingsForBooker.get(0);
        assertThat(booking1.getId(), equalTo(saveBookingDto2.getId()));
        assertThat(booking1.getStart(), equalTo(saveBookingDto2.getStart()));
        assertThat(booking1.getEnd(), equalTo(saveBookingDto2.getEnd()));
        assertThat(booking1.getItem().getId(), equalTo(saveBookingDto2.getItem().getId()));
        assertThat(booking1.getStatus(), equalTo(saveBookingDto2.getStatus()));

        BookingReadDto booking2 = allBookingsForBooker.get(1);
        assertThat(booking2.getId(), equalTo(saveBookingDto1.getId()));
        assertThat(booking2.getStart(), equalTo(saveBookingDto1.getStart()));
        assertThat(booking2.getEnd(), equalTo(saveBookingDto1.getEnd()));
        assertThat(booking2.getItem().getId(), equalTo(saveBookingDto1.getItem().getId()));
        assertThat(booking2.getStatus(), equalTo(saveBookingDto1.getStatus()));
    }

    @Test
    @DisplayName("Получние всех booking со статусом rejected для owner")
    void getAllRejectedBookingsForOwner() {
        BookingCreateDto bookingDto1 = makeBookingCreateDto(LocalDateTime.now().minus(Duration.ofDays(2)),
                LocalDateTime.now().minus(Duration.ofDays(1)), item1.getId());
        BookingCreateDto bookingDto2 = makeBookingCreateDto(LocalDateTime.now().plus(Duration.ofDays(3)),
                LocalDateTime.now().plus(Duration.ofDays(4)), item3.getId());
        BookingReadDto saveBookingDto1 = bookingService.saveBooking(user3.getId(), bookingDto1);
        BookingReadDto saveBookingDto2 = bookingService.saveBooking(user3.getId(), bookingDto2);
        bookingService.approvedBooking(user1.getId(), saveBookingDto1.getId(), false);
        bookingService.approvedBooking(user1.getId(), saveBookingDto2.getId(), false);

        List<BookingReadDto> allBookingsForBooker = bookingService.getBookingsForOwnerItem(user1.getId(),
                Status.REJECTED,
                0,
                10);

        BookingReadDto booking1 = allBookingsForBooker.get(0);
        assertThat(booking1.getId(), equalTo(saveBookingDto2.getId()));
        assertThat(booking1.getStart(), equalTo(saveBookingDto2.getStart()));
        assertThat(booking1.getEnd(), equalTo(saveBookingDto2.getEnd()));
        assertThat(booking1.getItem().getId(), equalTo(saveBookingDto2.getItem().getId()));
        assertThat(booking1.getStatus(), equalTo(Status.REJECTED));

        BookingReadDto booking2 = allBookingsForBooker.get(1);
        assertThat(booking2.getId(), equalTo(saveBookingDto1.getId()));
        assertThat(booking2.getStart(), equalTo(saveBookingDto1.getStart()));
        assertThat(booking2.getEnd(), equalTo(saveBookingDto1.getEnd()));
        assertThat(booking2.getItem().getId(), equalTo(saveBookingDto1.getItem().getId()));
        assertThat(booking2.getStatus(), equalTo(Status.REJECTED));
    }

    BookingCreateDto makeBookingCreateDto(LocalDateTime start, LocalDateTime end, Long itemId) {
        return BookingCreateDto.builder()
                .start(start)
                .end(end)
                .itemId(itemId)
                .build();
    }
}