package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestCreatDto;
import ru.practicum.shareit.request.dto.ItemRequestInfoDto;
import ru.practicum.shareit.request.dto.ItemRequestReadDto;
import ru.practicum.shareit.request.mapper.ItemRequestCreatMapper;
import ru.practicum.shareit.request.mapper.ItemRequestInfoMapper;
import ru.practicum.shareit.request.mapper.ItemRequestReadMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final ItemRequestCreatMapper itemRequestCreatMapper;
    private final ItemRequestReadMapper itemRequestReadMapper;
    private final ItemRequestInfoMapper itemRequestInfoMapper;


    @Override
    public ItemRequestReadDto save(ItemRequestCreatDto itemRequestReadDto, Long requesterId) {
        User requester = getUserById(requesterId);
        ItemRequest itemRequest = itemRequestRepository.save(
                itemRequestCreatMapper.toItemRequest(itemRequestReadDto, requester, LocalDateTime.now())
        );
        return itemRequestReadMapper.toRequestReadDto(itemRequest);
    }

    @Override
    public List<ItemRequestInfoDto> getItemRequestsByUserId(Long requesterId) {
        User user = getUserById(requesterId);
        List<ItemRequest> requests = itemRequestRepository.findAllByRequesterId(requesterId);
        Map<Long, List<ItemDto>> itemDtoMap = getItemDtoMap(requests);
        return requests.stream()
                .map(request -> itemRequestInfoMapper.toDto(request, itemDtoMap.get(request.getId())))
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemRequestInfoDto> getAllRequestItem(Long userId, Integer from, Integer size) {
        User user = getUserById(userId);
        Page<ItemRequest> requests = itemRequestRepository.findAllByRequesterIdNotOrderByCreatedDesc(
                userId, PageRequest.of(from, size)
        );
        Map<Long, List<ItemDto>> itemDtoMap = getItemDtoMap(requests.getContent());
        return requests.stream()
                .map(request -> itemRequestInfoMapper.toDto(request, itemDtoMap.get(request.getId())))
                .collect(Collectors.toList());
    }

    @Override
    public ItemRequestInfoDto getItemRequestsId(Long userId, Long requestId) {
        User user = getUserById(userId);
        ItemRequest itemRequest = getRequest(requestId);
        List<Item> items = itemRepository.findAllByRequestId(requestId);
        return itemRequestInfoMapper.toDto(itemRequest, items.stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList()));
    }

    private ItemRequest getRequest(Long requestId) {
        return itemRequestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("ItemRequest not found id: " + requestId));
    }

    private Map<Long, List<ItemDto>> getItemDtoMap(List<ItemRequest> requests) {
        List<Long> requestIds = requests.stream()
                .map(ItemRequest::getId)
                .collect(Collectors.toList());
        List<Item> items = itemRepository.findAllByRequestIds(requestIds);

        return items.stream()
                .collect(Collectors.groupingBy(
                        item -> item.getRequest().getId(),
                        Collectors.mapping(ItemMapper::toItemDto, Collectors.toList())
                ));
    }

    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found with id: " + id));
    }
}
