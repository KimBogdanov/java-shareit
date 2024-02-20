package ru.practicum.shareit.booking.model.enums;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

public enum Status {
    ALL, CURRENT, WAITING, APPROVED, REJECTED, CANCELED, PAST, FUTURE;

    public static Optional<Status> check(String status) {
        return Arrays.stream(Status.values())
                .filter(s -> Objects.equals(status.toUpperCase(), s.toString()))
                .findFirst();
    }
}
