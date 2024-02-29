package ru.practicum.shareit.request.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
@Builder
public class ItemRequestCreatDto {
    @NotBlank
    private String description;

    public ItemRequestCreatDto() {
    }
}
