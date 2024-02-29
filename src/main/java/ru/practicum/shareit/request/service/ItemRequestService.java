package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestCreatDto;
import ru.practicum.shareit.request.dto.ItemRequestReadDto;

public interface ItemRequestService {
    ItemRequestReadDto save(ItemRequestCreatDto itemRequestReadDto , Long userId);
}
