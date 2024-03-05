package ru.practicum.shareit.request.service;

import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.request.dto.ItemRequestCreatDto;
import ru.practicum.shareit.request.dto.ItemRequestInfoDto;
import ru.practicum.shareit.request.dto.ItemRequestReadDto;

import java.util.List;

@Transactional(readOnly = true)
public interface ItemRequestService {
    ItemRequestReadDto save(ItemRequestCreatDto itemRequestReadDto, Long requester);

    List<ItemRequestInfoDto> getItemRequestsByUserId(Long requesterId);

    List<ItemRequestInfoDto> getAllRequestItem(Long userId, Integer from, Integer size);

    ItemRequestInfoDto getItemRequestsId(Long userId, Long requestId);
}
