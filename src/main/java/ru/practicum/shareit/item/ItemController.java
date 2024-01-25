package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;

    @GetMapping()
    public List<ItemDto> getAllItemsByUserId(@RequestHeader("X-Sharer-User-Id") Integer userId) {
        return itemService.findAllItemsByUserId(userId);
    }

    @GetMapping("/{id}")
    public ItemDto getItem(@PathVariable Integer id) {
        return itemService.findItemById(id);
    }

    @PostMapping
    public ItemDto saveItem(@RequestHeader("X-Sharer-User-Id") Integer userId,
                            @Valid @RequestBody Item item) {
        log.info("Save item name: {}, owner id: {}", item.getName(), userId);
        return itemService.saveItem(item, userId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto patchItem(@RequestHeader("X-Sharer-User-Id") Integer userId,
                             @PathVariable Integer itemId,
                             @RequestBody Item item) {
        log.info("Patch item name: {}, owner id: {}", item.getId(), userId);
        return itemService.patchItem(item, itemId, userId);
    }
}
