package ru.practicum.shareit.exception.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.exception.ErrorResponse;
import ru.practicum.shareit.exception.InvalidStatusException;

@Slf4j
@RestControllerAdvice
public class ExceptionHandlerApi {
    @ExceptionHandler()
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(final MethodArgumentNotValidException e) {
        log.error(e.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse("Method Argument Not Valid", e.getMessage()));
    }

    @ExceptionHandler()
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(final IllegalArgumentException e) {
        log.error(e.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(e.getLocalizedMessage(), "Illegal Argument Exception"));
    }

    @ExceptionHandler()
    public ResponseEntity<ErrorResponse> handleInvalidStatusException(final InvalidStatusException e) {
        log.error(e.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(e.getLocalizedMessage(), "Invalid Status Exception"));
    }
}