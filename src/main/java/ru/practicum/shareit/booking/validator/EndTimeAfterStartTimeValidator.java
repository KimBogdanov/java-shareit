package ru.practicum.shareit.booking.validator;

import ru.practicum.shareit.booking.dto.BookingDto;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class EndTimeAfterStartTimeValidator implements ConstraintValidator<EndTimeAfterStartTime, BookingDto> {
    @Override
    public void initialize(EndTimeAfterStartTime constraintAnnotation) {
    }

    @Override
    public boolean isValid(BookingDto bookingDto, ConstraintValidatorContext constraintValidatorContext) {
        if (bookingDto == null || bookingDto.getStart() == null || bookingDto.getEnd() == null) {
            return true;
        }
        return bookingDto.getEnd().isAfter(bookingDto.getStart());
    }
}