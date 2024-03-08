package ru.practicum.shareit.booking;

import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingReadDto;
import ru.practicum.shareit.booking.model.enums.Status;
import ru.practicum.shareit.booking.service.BookingService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@WebMvcTest(BookingController.class)
@AutoConfigureMockMvc
class BookingControllerTest {
    //    private final ObjectMapper mapper = new ObjectMapper();
    @MockBean
    private BookingService bookingService;
    @Autowired
    private MockMvc mockMvc;
    BookingReadDto bookingReadDto;

    @BeforeEach
    void setUp() {
        bookingReadDto = BookingReadDto.builder()
                .id(1L)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .item(BookingReadDto.ItemNameDto.builder()
                        .id(1L)
                        .name("item")
                        .build())
                .booker(BookingReadDto.UserIdDto.builder()
                        .id(1L)
                        .build())
                .status(Status.WAITING)
                .build();
    }

    @Test
    @SneakyThrows
    @DisplayName("Добавление booking")
    void saveBooking() {
        BookingCreateDto bookingCreateDto = BookingCreateDto.builder()
                .start(bookingReadDto.getStart())
                .end(bookingReadDto.getEnd())
                .itemId(bookingReadDto.getItem().getId()).build();
        when(bookingService.saveBooking(any(), any()))
                .thenReturn(bookingReadDto);

        mockMvc.perform(post("/bookings")
//                        .content(mapper.writeValueAsString(bookingCreateDto))
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header("X-Sharer-User-Id", 1));
//                .andExpect(status().isOk());
//                .andExpect(jsonPath("$.id").value(bookingReadDto.getId()))
//                .andExpect(jsonPath("$.itemId").value(bookingReadDto.getItem().getId()))
//                .andExpect(jsonPath("$.status").value(bookingReadDto.getStatus().toString()));
    }

    @Test
    void approvedBooking() {
    }

    @Test
    void getStatus() {
    }

    @Test
    void getBookings() {
    }

    @Test
    void getBookingItem() {
    }
}