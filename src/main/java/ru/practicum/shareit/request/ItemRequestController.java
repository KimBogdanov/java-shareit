package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestCreatDto;
import ru.practicum.shareit.request.dto.ItemRequestReadDto;
import ru.practicum.shareit.request.service.ItemRequestService;

@Slf4j
@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {
    private final ItemRequestService itemRequestService;

    @PostMapping
    public ItemRequestReadDto saveItemRequest(@RequestHeader("X-Sharer-User-Id") Long userId,
                                              @RequestBody ItemRequestCreatDto dto) {
        log.info("SaveItemRequest user id: {}, request description {}", userId, dto.getDescription());
        return itemRequestService.save(dto, userId);
    }
}
