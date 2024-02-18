package ru.practicum.shareit.booking.validator;

import ru.practicum.shareit.booking.dto.BookingDto;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.lang.annotation.Annotation;

public class EndTimeAfterStartTimeValidator implements ConstraintValidator<EndTimeAfterStartTime, BookingDto> {
    @Override
    public void initialize(EndTimeAfterStartTime constraintAnnotation) {
    }

    @Override
    public boolean isValid(BookingDto bookingDto, ConstraintValidatorContext constraintValidatorContext) {
        if (bookingDto == null) {
            return true;
        }

        return bookingDto.getEndTime().isAfter(bookingDto.getStartTime());
    }
}
