package ru.practicum.shareit.booking;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingReadDto;
import ru.practicum.shareit.booking.model.enums.Status;
import ru.practicum.shareit.booking.service.BookingService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookingController.class)
@AutoConfigureMockMvc
class BookingControllerTest {
    public final String START = "2026-03-11T11:44:51.000000000";
    public final String END = "2026-03-12T11:44:51.000000000";
    @Autowired
    ObjectMapper mapper;
    @MockBean
    private BookingService bookingService;
    private final DateTimeFormatter pattern = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
    @Autowired
    private MockMvc mockMvc;

    @Test
    @SneakyThrows
    @DisplayName("Тест метода saveBooking")
    void saveBooking() {
        Long bookerId = 1L;
        Long itemId = 1L;
        LocalDateTime startTime = LocalDateTime.parse(START);
        LocalDateTime endTime = LocalDateTime.parse(END);
        BookingCreateDto bookingCreateDto = getBookingCreateDto(startTime, endTime, itemId);
        BookingReadDto bookingReadDto = getBookingReadDto(bookerId, bookingCreateDto, Status.WAITING);

        when(bookingService.saveBooking(any(), any()))
                .thenReturn(bookingReadDto);

        mockMvc.perform(post("/bookings")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(bookingCreateDto))
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", bookerId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(bookingReadDto.getId()))
                .andExpect(jsonPath("$.start").value(bookingReadDto.getStart().format(pattern)))
                .andExpect(jsonPath("$.end").value(bookingReadDto.getEnd().format(pattern)))
                .andExpect(jsonPath("$.item.id").value(bookingReadDto.getItem().getId()))
                .andExpect(jsonPath("$.item.name").value(bookingReadDto.getItem().getName()))
                .andExpect(jsonPath("$.booker.id").value(bookingReadDto.getBooker().getId()))
                .andExpect(jsonPath("$.status").value(bookingReadDto.getStatus().toString()));

        Mockito.verify(bookingService,
                        Mockito.times(1))
                .saveBooking(bookerId, bookingCreateDto);
    }

    @Test
    @SneakyThrows
    @DisplayName("Добавление booking без указния Start")
    void saveBookingWithoutStart() {
        Long bookerId = 1L;
        Long itemId = 1L;
        LocalDateTime startTime = null;
        LocalDateTime endTime = LocalDateTime.parse(END);
        BookingCreateDto bookingCreateDto = getBookingCreateDto(startTime, endTime, itemId);
        mockMvc.perform(post("/bookings")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(bookingCreateDto))
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", bookerId))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Method Argument Not Valid"));
    }

    @Test
    @SneakyThrows
    @DisplayName("Добавление booking без указния End")
    void saveBookingWithoutEnd() {
        Long bookerId = 1L;
        Long itemId = 1L;
        LocalDateTime startTime = LocalDateTime.parse(START);
        LocalDateTime endTime = null;
        BookingCreateDto bookingCreateDto = getBookingCreateDto(startTime, endTime, itemId);
        mockMvc.perform(post("/bookings")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(bookingCreateDto))
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", bookerId))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Method Argument Not Valid"));
    }

    @Test
    @SneakyThrows
    @DisplayName("Добавление booking без указния ItemId")
    void saveBookingWithoutItemId() {
        Long bookerId = 1L;
        Long itemId = null;
        LocalDateTime startTime = LocalDateTime.parse(START);
        LocalDateTime endTime = LocalDateTime.parse(END);
        BookingCreateDto bookingCreateDto = getBookingCreateDto(startTime, endTime, itemId);
        mockMvc.perform(post("/bookings")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(bookingCreateDto))
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", bookerId))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Method Argument Not Valid"));
    }

    @Test
    @SneakyThrows
    @DisplayName("Добавление booking Start в прошлом")
    void saveBookingStartInThePast() {
        Long bookerId = 1L;
        Long itemId = 1L;
        LocalDateTime startTime = LocalDateTime.parse("2020-03-11T11:44:51.000000000");
        LocalDateTime endTime = LocalDateTime.parse(END);
        BookingCreateDto bookingCreateDto = getBookingCreateDto(startTime, endTime, itemId);
        mockMvc.perform(post("/bookings")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(bookingCreateDto))
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", bookerId))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Method Argument Not Valid"));
    }

    @Test
    @SneakyThrows
    @DisplayName("Добавление booking End раньше Start")
    void saveBookingEndEarlyStart() {
        Long bookerId = 1L;
        Long itemId = 1L;
        LocalDateTime startTime = LocalDateTime.parse(END);
        LocalDateTime endTime = LocalDateTime.parse(START);
        BookingCreateDto bookingCreateDto = getBookingCreateDto(startTime, endTime, itemId);
        mockMvc.perform(post("/bookings")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(bookingCreateDto))
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", bookerId))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Method Argument Not Valid"));
    }

    @Test
    @SneakyThrows
    @DisplayName("Подтверждение статуса booking")
    void approvedBooking() {
        Long bookerId = 1L;
        Long bookingId = 4L;
        Long itemId = 2L;
        boolean approved = true;
        Long ownerId = 3L;
        LocalDateTime startTime = LocalDateTime.parse(START);
        LocalDateTime endTime = LocalDateTime.parse(END);
        BookingCreateDto bookingCreateDto = getBookingCreateDto(startTime, endTime, itemId);
        BookingReadDto bookingReadDto = getBookingReadDto(bookerId, bookingCreateDto, Status.APPROVED);

        when(bookingService.approvedBooking(anyLong(), anyLong(), anyBoolean()))
                .thenReturn(bookingReadDto);

        mockMvc.perform(patch("/bookings/{bookingId}", bookingId)
                        .param("approved", Boolean.toString(approved))
                        .header("X-Sharer-User-Id", ownerId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(bookingReadDto.getId()))
                .andExpect(jsonPath("$.start").value(bookingReadDto.getStart().format(pattern)))
                .andExpect(jsonPath("$.end").value(bookingReadDto.getEnd().format(pattern)))
                .andExpect(jsonPath("$.item.id").value(bookingReadDto.getItem().getId()))
                .andExpect(jsonPath("$.item.name").value(bookingReadDto.getItem().getName()))
                .andExpect(jsonPath("$.booker.id").value(bookingReadDto.getBooker().getId()))
                .andExpect(jsonPath("$.status").value(bookingReadDto.getStatus().toString()));

        Mockito.verify(bookingService,
                        Mockito.times(1))
                .approvedBooking(ownerId, bookingId, approved);
    }

    @Test
    @SneakyThrows
    @DisplayName("Получение статуса booking")
    void getStatus() {
        Long bookerId = 1L;
        Long itemId = 2L;
        Long ownerId = 3L;
        Long bookingId = 4L;
        LocalDateTime startTime = LocalDateTime.parse(START);
        LocalDateTime endTime = LocalDateTime.parse(END);
        BookingCreateDto bookingCreateDto = getBookingCreateDto(startTime, endTime, itemId);
        BookingReadDto bookingReadDto = getBookingReadDto(bookerId, bookingCreateDto, Status.APPROVED);

        when(bookingService.getStatus(anyLong(), anyLong()))
                .thenReturn(bookingReadDto);

        mockMvc.perform(get("/bookings/{bookingId}", bookingId)
                        .header("X-Sharer-User-Id", ownerId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(bookingReadDto.getId()))
                .andExpect(jsonPath("$.start").value(bookingReadDto.getStart().format(pattern)))
                .andExpect(jsonPath("$.end").value(bookingReadDto.getEnd().format(pattern)))
                .andExpect(jsonPath("$.item.id").value(bookingReadDto.getItem().getId()))
                .andExpect(jsonPath("$.item.name").value(bookingReadDto.getItem().getName()))
                .andExpect(jsonPath("$.booker.id").value(bookingReadDto.getBooker().getId()))
                .andExpect(jsonPath("$.status").value(bookingReadDto.getStatus().toString()));

        Mockito.verify(bookingService, Mockito.times(1))
                .getStatus(ownerId, bookingId);
    }

    @Test
    @SneakyThrows
    @DisplayName("Получение всех bookings по bookerId")
    void getAllBookingsForBooker() {
        Long bookerId = 1L;
        Long itemId = 2L;
        Integer from = 0;
        Integer size = 1;
        Status state = Status.ALL;
        LocalDateTime startTime = LocalDateTime.parse(START);
        LocalDateTime endTime = LocalDateTime.parse(END);
        BookingCreateDto bookingCreateDto = getBookingCreateDto(startTime, endTime, itemId);
        BookingReadDto bookingReadDto = getBookingReadDto(bookerId, bookingCreateDto, Status.APPROVED);
        List<BookingReadDto> listReadDto = new ArrayList<>();
        listReadDto.add(bookingReadDto);

        when(bookingService
                .getAllBookingsForBooker(anyLong(), any(Status.class), anyInt(), anyInt()))
                .thenReturn(listReadDto);

        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", bookerId)
                        .param("from", from.toString())
                        .param("size", size.toString())
                        .param("state", state.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id").value(bookingReadDto.getId()))
                .andExpect(jsonPath("$.[0].start").value(bookingReadDto.getStart().format(pattern)))
                .andExpect(jsonPath("$.[0].end").value(bookingReadDto.getEnd().format(pattern)))
                .andExpect(jsonPath("$.[0].item.id").value(bookingReadDto.getItem().getId()))
                .andExpect(jsonPath("$.[0].item.name").value(bookingReadDto.getItem().getName()))
                .andExpect(jsonPath("$.[0].booker.id").value(bookingReadDto.getBooker().getId()))
                .andExpect(jsonPath("$.[0].status").value(bookingReadDto.getStatus().toString()));

        Mockito.verify(bookingService, Mockito.times(1))
                .getAllBookingsForBooker(bookerId, state, from, size);
    }

    @Test
    @SneakyThrows
    @DisplayName("Получение всех bookings по bookerId с некорректным from")
    void getAllBookingsForBookerWithIncorrectReqParam() {
        Long bookerId = 1L;
        Integer from = -1;
        Integer size = 1;
        Status state = Status.ALL;

        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", bookerId)
                        .param("from", from.toString())
                        .param("size", size.toString())
                        .param("state", state.toString()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Request param incorrect"))
                .andExpect(jsonPath("$.description").value("Illegal Argument Exception"));

        Mockito.verify(bookingService, Mockito.never())
                .getAllBookingsForBooker(bookerId, state, from, size);
    }

    @Test
    @SneakyThrows
    @DisplayName("Получение всех bookings по bookerId с некорректным state")
    void getAllBookingsForBookerWithIncorrectState() {
        Long bookerId = 1L;
        int from = 0;
        int size = 1;
        String state = "incorrect";

        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", bookerId)
                        .param("from", Integer.toString(from))
                        .param("size", Integer.toString(size))
                        .param("state", state))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Unknown state: " + state))
                .andExpect(jsonPath("$.description").value("Illegal Argument Exception"));

        Mockito.verify(bookingService, Mockito.never())
                .getAllBookingsForBooker(anyLong(), any(Status.class), anyInt(), anyInt());
    }

    @Test
    @SneakyThrows
    @DisplayName("Получение всех bookings по ownerId")
    void getAllBookingsForOwner() {
        Long ownerId = 1L;
        Long itemId = 2L;
        Integer from = 0;
        Integer size = 1;
        Status state = Status.ALL;
        LocalDateTime startTime = LocalDateTime.parse(START);
        LocalDateTime endTime = LocalDateTime.parse(END);
        BookingCreateDto bookingCreateDto = getBookingCreateDto(startTime, endTime, itemId);
        BookingReadDto bookingReadDto = getBookingReadDto(ownerId, bookingCreateDto, Status.APPROVED);
        List<BookingReadDto> listReadDto = new ArrayList<>();
        listReadDto.add(bookingReadDto);

        when(bookingService
                .getBookingsForOwnerItem(anyLong(), any(Status.class), anyInt(), anyInt()))
                .thenReturn(listReadDto);

        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", ownerId)
                        .param("from", from.toString())
                        .param("size", size.toString())
                        .param("state", state.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id").value(bookingReadDto.getId()))
                .andExpect(jsonPath("$.[0].start").value(bookingReadDto.getStart().format(pattern)))
                .andExpect(jsonPath("$.[0].end").value(bookingReadDto.getEnd().format(pattern)))
                .andExpect(jsonPath("$.[0].item.id").value(bookingReadDto.getItem().getId()))
                .andExpect(jsonPath("$.[0].item.name").value(bookingReadDto.getItem().getName()))
                .andExpect(jsonPath("$.[0].booker.id").value(bookingReadDto.getBooker().getId()))
                .andExpect(jsonPath("$.[0].status").value(bookingReadDto.getStatus().toString()));

        Mockito.verify(bookingService, Mockito.times(1))
                .getBookingsForOwnerItem(ownerId, state, from, size);
    }

    @Test
    @SneakyThrows
    @DisplayName("Получение всех bookings по ownerId с некорректным from")
    void getAllBookingsForOwnerWithIncorrectReqParam() {
        Long ownerId = 1L;
        Integer from = -1;
        Integer size = 1;
        Status state = Status.ALL;

        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", ownerId)
                        .param("from", from.toString())
                        .param("size", size.toString())
                        .param("state", state.toString()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Request param incorrect"))
                .andExpect(jsonPath("$.description").value("Illegal Argument Exception"));

        Mockito.verify(bookingService, Mockito.never())
                .getBookingsForOwnerItem(ownerId, state, from, size);
    }

    @Test
    @SneakyThrows
    @DisplayName("Получение всех bookings по ownerId с некорректным state")
    void getAllBookingsForOwnerWithIncorrectState() {
        Long ownerId = 1L;
        int from = 0;
        int size = 1;
        String state = "incorrect";

        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", ownerId)
                        .param("from", Integer.toString(from))
                        .param("size", Integer.toString(size))
                        .param("state", state))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Unknown state: " + state))
                .andExpect(jsonPath("$.description").value("Illegal Argument Exception"));

        Mockito.verify(bookingService, Mockito.never())
                .getBookingsForOwnerItem(anyLong(), any(Status.class), anyInt(), anyInt());
    }

    private static BookingCreateDto getBookingCreateDto(LocalDateTime startTime, LocalDateTime endTime, Long itemId) {
        return BookingCreateDto.builder()
                .start(startTime)
                .end(endTime)
                .itemId(itemId)
                .build();
    }

    private static BookingReadDto getBookingReadDto(Long bookerId, BookingCreateDto bookingCreateDto, Status status) {
        return BookingReadDto.builder()
                .id(1L)
                .start(bookingCreateDto.getStart())
                .end(bookingCreateDto.getEnd())
                .item(BookingReadDto.ItemNameDto.builder()
                        .id(bookingCreateDto.getItemId())
                        .name("item")
                        .build())
                .booker(BookingReadDto.UserIdDto.builder()
                        .id(bookerId)
                        .build())
                .status(status)
                .build();
    }
}