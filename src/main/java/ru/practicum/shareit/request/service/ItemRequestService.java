package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestCreatDto;
import ru.practicum.shareit.request.dto.ItemRequestInfoDto;
import ru.practicum.shareit.request.dto.ItemRequestReadDto;

import java.util.List;

public interface ItemRequestService {
    ItemRequestReadDto save(ItemRequestCreatDto itemRequestReadDto, Long requester);

    List<ItemRequestInfoDto> getAllRequestsByRequesterId(Long requesterId);

    List<ItemRequestInfoDto> getAllRequestItem(Long userId, Integer from, Integer size);

    ItemRequestInfoDto getItemRequestById(Long userId, Long requestId);
}
