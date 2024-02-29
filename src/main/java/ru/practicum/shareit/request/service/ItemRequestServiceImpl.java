package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.request.dto.ItemRequestCreatDto;
import ru.practicum.shareit.request.dto.ItemRequestReadDto;
import ru.practicum.shareit.request.mapper.ItemRequestCreatMapper;
import ru.practicum.shareit.request.mapper.ItemRequestReadMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;
    private final ItemRequestCreatMapper itemRequestCreatMapper;
    private final ItemRequestReadMapper itemRequestReadMapper;

    @Override
    public ItemRequestReadDto save(ItemRequestCreatDto itemRequestReadDto, Long userId) {
        User user = getUserById(userId);
        ItemRequest itemRequest = itemRequestRepository.save(
                itemRequestCreatMapper.toItemRequest(itemRequestReadDto, user, LocalDateTime.now())
        );
        return itemRequestReadMapper.toRequestReadDto(itemRequest);
    }

    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found with id: " + id));
    }
}
