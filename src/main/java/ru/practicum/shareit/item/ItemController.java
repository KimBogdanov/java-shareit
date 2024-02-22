package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.comment.dto.CommentCreateDto;
import ru.practicum.shareit.item.comment.service.CommentService;
import ru.practicum.shareit.item.dto.ItemBookingDto;
import ru.practicum.shareit.item.dto.ItemBookingProjection;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;
    private final CommentService commentService;

    /**
     * Получение списка всех вещей пользователя по его идентификатору.
     *
     * @param userId Идентификатор пользователя-владельца вещей.
     * @return Список объектов {@link ItemDto}, описывающих вещи пользователя.
     */
    @GetMapping()
    public List<ItemBookingProjection> getAllItemsByUserId(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Get all the user's items. User id: {}", userId);
        return itemService.findAllItemsByUserId(userId);
    }

    /**
     * Получение информации о конкретной вещи по её идентификатору.
     *
     * @param id Идентификатор вещи.
     * @return Объект {@link ItemBookingDto}, описывающий вещь.
     */
    @GetMapping("/{id}")
    public ItemBookingDto getItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                                  @PathVariable Long id) {
        log.info("Get item id {} for user id {}", id, userId);
        return itemService.getItemDtoById(id, userId);
    }

    /**
     * Поиск вещей по тексту.
     *
     * @param userId Идентификатор пользователя, выполняющего поиск.
     * @param text   Текст для поиска в названии или описании вещей.
     * @return Список объектов {@link ItemDto}, удовлетворяющих критериям поиска.
     */
    @GetMapping("/search")
    public List<ItemDto> searchItems(@RequestHeader("X-Sharer-User-Id") Long userId,
                                     @RequestParam String text) {
        log.info("User: {} search item by string: {}", userId, text);
        return itemService.searchByString(text, userId);
    }

    /**
     * Добавление новой вещи.
     *
     * @param userId  Идентификатор пользователя-владельца вещи.
     * @param itemDto Объект {@link ItemDto}, описывающий новую вещь.
     * @return Объект {@link ItemDto}, представляющий сохраненную вещь.
     */
    @PostMapping
    public ItemDto saveItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                            @Valid @RequestBody ItemDto itemDto) {
        log.info("Save item name: {}, owner id: {}", itemDto.getName(), userId);
        return itemService.saveItem(itemDto, userId);
    }

    /**
     * Редактирование вещи.
     *
     * @param userId  Идентификатор пользователя-владельца вещи.
     * @param itemId  Идентификатор вещи, которую нужно отредактировать.
     * @param itemDto Объект {@link ItemDto}, содержащий поля для редактирования.
     * @return Объект {@link ItemDto}, представляющий отредактированную вещь.
     */
    @PatchMapping("/{itemId}")
    public ItemDto patchItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                             @PathVariable Long itemId,
                             @RequestBody ItemDto itemDto) {
        log.info("Patch item name: {}, owner id: {}", itemDto.getId(), userId);
        return itemService.patchItem(itemDto, itemId, userId);
    }
    @PostMapping({"/{itemId}/comment"})
    public CommentCreateDto saveComment(@RequestHeader("X-Sharer-User-Id") Long userId,
                                        @PathVariable Long itemId,
                                        @Valid @RequestBody CommentCreateDto commentCreateDto) {
        log.info("Save comment item id");
        return commentService.saveComment(userId, itemId, commentCreateDto);
    }
}
