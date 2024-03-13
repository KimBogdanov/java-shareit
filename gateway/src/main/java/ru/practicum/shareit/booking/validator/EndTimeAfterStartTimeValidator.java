package ru.practicum.shareit.booking.validator;

import ru.practicum.shareit.booking.dto.BookItemRequestDto;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class EndTimeAfterStartTimeValidator implements ConstraintValidator<EndTimeAfterStartTime, BookItemRequestDto> {
    @Override
    public void initialize(EndTimeAfterStartTime constraintAnnotation) {
    }

    @Override
    public boolean isValid(BookItemRequestDto bookingCreateDto, ConstraintValidatorContext constraintValidatorContext) {
        if (bookingCreateDto == null || bookingCreateDto.getStart() == null || bookingCreateDto.getEnd() == null) {
            return true;
        }
        return bookingCreateDto.getEnd().isAfter(bookingCreateDto.getStart());
    }
}