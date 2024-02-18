package ru.practicum.shareit.booking.dto;

import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import ru.practicum.shareit.booking.validator.EndTimeAfterStartTime;

import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Data
@RequiredArgsConstructor
@Builder
@EndTimeAfterStartTime
public class BookingDto {
    @FutureOrPresent
    private final LocalDateTime startTime;
    @Future
    private final LocalDateTime endTime;
    @NotBlank
    private final Long itemId;
}
