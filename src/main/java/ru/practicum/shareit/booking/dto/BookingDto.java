package ru.practicum.shareit.booking.dto;

import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import ru.practicum.shareit.booking.validator.EndTimeAfterStartTime;

import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@RequiredArgsConstructor
@Builder
@EndTimeAfterStartTime
public class BookingDto {
    private final Long id;
    @NotNull
    @FutureOrPresent
    private final LocalDateTime start;
    @NotNull
    @Future
    private final LocalDateTime end;
    @NotNull
    private final Long itemId;
}
