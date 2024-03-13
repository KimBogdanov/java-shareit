package ru.practicum.shareit.exception;

public class NotBelongToUser extends RuntimeException {
    public NotBelongToUser(String message) {
        super(message);
    }
}