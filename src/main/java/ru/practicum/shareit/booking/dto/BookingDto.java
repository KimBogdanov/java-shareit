package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.validator.EndTimeAfterStartTime;

import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@EndTimeAfterStartTime
public class BookingDto {
    private Long id;
    @NotNull
    @FutureOrPresent
    private LocalDateTime start;
    @NotNull
    @Future
    private LocalDateTime end;
    @NotNull
    private Long itemId;

    public BookingDto() {
    }
}
