package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestCreatDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Slf4j
@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {
    public final String userIdHeader = "X-Sharer-User-Id";
    private final RequestClient requestClient;

    @PostMapping
    public ResponseEntity<Object> saveItemRequest(@RequestHeader(userIdHeader) Long requesterId,
                                                  @Valid @RequestBody ItemRequestCreatDto dto) {
        log.info("SaveItemRequest user id: {}, request description {}", requesterId, dto.getDescription());
        return requestClient.save(dto, requesterId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllRequestsByRequesterId(@RequestHeader(userIdHeader) Long requesterId) {
        log.info("getAllRequesterRequest requester id: {}", requesterId);
        return requestClient.getAllRequestsByRequesterId(requesterId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllRequests(@RequestHeader(userIdHeader) Long userId,
                                                 @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                                 @Positive @RequestParam(defaultValue = "10") Integer size) {
        log.info("getAllRequestItem user id {}, from: {}, size: {}", userId, from, size);
        return requestClient.getAllRequestItem(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getRequestByRequestId(@RequestHeader(userIdHeader) Long userId,
                                                        @PathVariable Long requestId) {
        log.info("getItemRequestInfoDto user id: {}, request id: {}", userId, requestId);
        return requestClient.getItemRequestById(userId, requestId);
    }
}

