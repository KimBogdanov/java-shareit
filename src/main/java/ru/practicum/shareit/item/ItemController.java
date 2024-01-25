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
//    @GetMapping()
//    public List<ItemDto> getAllItems() {
//        return itemService.findAllItemDTO();
//    }

    @PostMapping
    public ItemDto saveItem(@RequestHeader("X-Sharer-User-Id") Integer userId,
                            @Valid @RequestBody Item item) {
        log.info("Save item name: {}, owner id: {}", item.getName(), userId);
        item.setOwnerId(userId);
        return itemService.saveItem(item);
    }
}
