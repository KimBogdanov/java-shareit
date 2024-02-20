package ru.practicum.shareit.exception.handler;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.exception.*;

import java.util.List;
import java.util.stream.Collectors;

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
        List<String> errorMessages = e.getBindingResult().getFieldErrors().stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.toList());
        log.error(e.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse("Method Argument Not Valid", e.getMessage()));
    }

    @ExceptionHandler()
    public ResponseEntity<ErrorResponse> handleConstraintViolationException(final ConstraintViolationException e) {
        log.error(e.getMessage());
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(new ErrorResponse("Constraint Violation Exception", e.getMessage()));
    }

    @ExceptionHandler()
    public ResponseEntity<ErrorResponse> handleNotAvailableException(final NotAvailableException e) {
        log.error(e.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse("Not Available Exception", e.getMessage()));
    }

    @ExceptionHandler()
    public ResponseEntity<ErrorResponse> handleNotOwnedException(final NotOwnedException e) {
        log.error(e.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse("Not Owned Exception", e.getMessage()));
    }

    @ExceptionHandler()
    public ResponseEntity<ErrorResponse> handleBookingExpiredException(final BookingExpiredException e) {
        log.error(e.getMessage());
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(new ErrorResponse("Booking Expired Exception", e.getMessage()));
    }
}