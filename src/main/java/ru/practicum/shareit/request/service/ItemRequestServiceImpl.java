package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemCreateEditDto;
import ru.practicum.shareit.item.mapper.ItemCreateEditMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.pageRequest.PageRequestChangePageToFrom;
import ru.practicum.shareit.request.dto.ItemRequestCreatDto;
import ru.practicum.shareit.request.dto.ItemRequestInfoDto;
import ru.practicum.shareit.request.dto.ItemRequestReadDto;
import ru.practicum.shareit.request.mapper.ItemRequestCreatMapper;
import ru.practicum.shareit.request.mapper.ItemRequestInfoMapper;
import ru.practicum.shareit.request.mapper.ItemRequestReadMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository itemRequestRepository;
    private final UserService userService;
    private final ItemRepository itemRepository;
    private final ItemRequestCreatMapper itemRequestCreatMapper;
    private final ItemRequestReadMapper itemRequestReadMapper;
    private final ItemRequestInfoMapper itemRequestInfoMapper;
    private final ItemCreateEditMapper itemCreateEditMapper;

    @Override
    @Transactional
    public ItemRequestReadDto save(ItemRequestCreatDto itemRequestReadDto, Long requesterId) {
        User requester = userService.getUserOrThrowException(requesterId);

        return Optional.of(itemRequestReadDto)
                .map(request -> itemRequestCreatMapper.toItemRequest(itemRequestReadDto, requester, LocalDateTime.now()))
                .map(itemRequestRepository::save)
                .map(itemRequestReadMapper::toRequestReadDto)
                .orElseThrow(RuntimeException::new);
    }

    @Override
    public List<ItemRequestInfoDto> getAllRequestsByRequesterId(Long requesterId) {
        userService.getUserOrThrowException(requesterId);

        List<ItemRequest> requests = itemRequestRepository.findAllByRequesterIdOrderByCreated(requesterId);
        Map<Long, List<ItemCreateEditDto>> itemDtoMap = getItemDtoMap(requests);

        return requests.stream()
                .map(request -> itemRequestInfoMapper.toDto(
                        request,
                        itemDtoMap.get(request.getId()) == null ? // надо покумекать
                                Collections.emptyList() : itemDtoMap.get(request.getId())))
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemRequestInfoDto> getAllRequestItem(Long userId, Integer from, Integer size) {
        userService.getUserOrThrowException(userId);
        Page<ItemRequest> requests = itemRequestRepository.findAllByRequesterIdNot(
                userId,
                new PageRequestChangePageToFrom(from, size, Sort.by(Sort.Order.desc("created")))
        );
        Map<Long, List<ItemCreateEditDto>> itemDtoMap = getItemDtoMap(requests.getContent());
        return requests.stream()
                .map(request -> itemRequestInfoMapper.toDto(request, itemDtoMap.get(request.getId())))
                .collect(Collectors.toList());
    }

    @Override
    public ItemRequestInfoDto getItemRequestById(Long userId, Long requestId) {
        userService.getUserOrThrowException(userId);
        ItemRequest itemRequest = getRequest(requestId);
        List<Item> items = itemRepository.findAllByRequestId(requestId);
        return itemRequestInfoMapper.toDto(
                itemRequest, items.stream()
                .map(itemCreateEditMapper::toItemCreateEditDto)
                .collect(Collectors.toList()));
    }

    private ItemRequest getRequest(Long requestId) {
        return itemRequestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("ItemRequest not found id: " + requestId));
    }

    private Map<Long, List<ItemCreateEditDto>> getItemDtoMap(List<ItemRequest> requests) {
        List<Long> requestIds = requests.stream()
                .map(ItemRequest::getId)
                .collect(Collectors.toList());
        List<Item> items = itemRepository.findAllByRequestIds(requestIds);

        return items.stream()
                .collect(Collectors.groupingBy(
                        item -> item.getRequest().getId(),
                        Collectors.mapping(itemCreateEditMapper::toItemCreateEditDto, Collectors.toList())
                ));
    }
}
