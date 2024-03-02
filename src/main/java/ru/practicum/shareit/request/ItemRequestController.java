package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestCreatDto;
import ru.practicum.shareit.request.dto.ItemRequestInfoDto;
import ru.practicum.shareit.request.dto.ItemRequestReadDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {
    private final ItemRequestService itemRequestService;

    @PostMapping
    public ItemRequestReadDto saveItemRequest(@RequestHeader("X-Sharer-User-Id") Long requesterId,
                                              @Valid @RequestBody ItemRequestCreatDto dto) {
        log.info("SaveItemRequest user id: {}, request description {}", requesterId, dto.getDescription());
        return itemRequestService.save(dto, requesterId);
    }

    @GetMapping
    public List<ItemRequestInfoDto> getRequesterItemRequests(@RequestHeader("X-Sharer-User-Id") Long requesterId) {
        log.info("getOwnerItemRequests user id: {}", requesterId);
        return itemRequestService.getItemRequestsByUserId(requesterId);
    }

    @GetMapping("/all")
    public List<ItemRequestInfoDto> getAllRequestItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                      @RequestParam(defaultValue = "0") Integer from,
                                                      @RequestParam(defaultValue = "10") Integer size) {
        log.info("getAllRequestItem user id {}, from: {}, size: {}", userId, from, size);
        if (from < 0 || size < 1) {
            throw new IllegalArgumentException("Request param incorrect");
        }

        return itemRequestService.getAllRequestItem(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ItemRequestInfoDto getItemRequestInfoDto(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                    @PathVariable Long requestId) {
        log.info("getItemRequestInfoDto user id: {}, request id: {}", userId, requestId);
        return itemRequestService.getItemRequestsId(userId, requestId);
    }
}

