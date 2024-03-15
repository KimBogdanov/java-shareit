package ru.practicum.shareit.exception;

public class NotOwnedException extends RuntimeException {
    public NotOwnedException(String message) {
        super(message);
    }
}