package ru.practicum.shareit.exception.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.exception.AlreadyExistsException;
import ru.practicum.shareit.exception.ErrorResponse;
import ru.practicum.shareit.exception.NotBelongToUser;
import ru.practicum.shareit.exception.NotFoundException;

@Slf4j
@RestControllerAdvice
public class ExceptionHandlerApi {
    @ExceptionHandler()
    public ResponseEntity<ErrorResponse> handleNotFoundException(final NotFoundException e) {
        log.error(e.getMessage());
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse("Not found", e.getMessage()));
    }

    @ExceptionHandler()
    public ResponseEntity<ErrorResponse> handleNotBelongToUser(final NotBelongToUser e) {
        log.error(e.getMessage());
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse("Not Belong To User", e.getMessage()));
    }

    @ExceptionHandler()
    public ResponseEntity<ErrorResponse> handleAlreadyExistException(final AlreadyExistsException e) {
        log.error(e.getMessage());
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(new ErrorResponse("Already Exist", e.getMessage()));
    }

    @ExceptionHandler()
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(final MethodArgumentNotValidException e) {
        log.error(e.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse("Method Argument Not Valid", e.getMessage()));
    }
}