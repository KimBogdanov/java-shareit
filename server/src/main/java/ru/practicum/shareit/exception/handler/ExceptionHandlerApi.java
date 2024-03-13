package ru.practicum.shareit.exception.handler;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.exception.*;

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
                .status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse("Not Owned Exception", e.getMessage()));
    }

    @ExceptionHandler()
    public ResponseEntity<ErrorResponse> handleBookingExpiredException(final BookingExpiredException e) {
        log.error(e.getMessage());
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(new ErrorResponse("Booking Expired Exception", e.getMessage()));
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