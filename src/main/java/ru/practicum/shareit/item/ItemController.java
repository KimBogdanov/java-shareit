package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.comment.dto.CommentCreateDto;
import ru.practicum.shareit.item.comment.dto.CommentReadDto;
import ru.practicum.shareit.item.comment.service.CommentService;
import ru.practicum.shareit.item.dto.ItemReadDto;
import ru.practicum.shareit.item.dto.ItemCreateEditDto;
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
     * Получает список товаров, связанных с указанным пользователем.
     *
     * @param userId Идентификатор пользователя, предметы которого необходимо получить.
     * @param from   Начальный индекс вещи для постраничных результатов (по умолчанию 0).
     * @param size   Максимальное количество вещей на странице (по умолчанию 10).
     * @return Список {@link ItemReadDto}, представляющих вещи, связанные с пользователем.
     * @throws IllegalArgumentException Если параметры 'from' или 'size' являются недопустимыми.
     */
    @GetMapping()
    public List<ItemReadDto> getAllItemsByUserId(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                 @RequestParam(defaultValue = "0") Integer from,
                                                 @RequestParam(defaultValue = "10") Integer size) {
        log.info("Get all the user's items. User id: {}", userId);
        checkRequestParamAndThrowException(from, size);
        return itemService.findAllItemsByUserId(userId, from, size);
    }

    /**
     * Получение информации о конкретной вещи по её идентификатору.
     *
     * @param itemId Идентификатор вещи.
     * @return Объект {@link ItemReadDto}, описывающий вещь.
     */
    @GetMapping("/{itemId}")
    public ItemReadDto getItemById(@RequestHeader("X-Sharer-User-Id") Long userId,
                                   @PathVariable Long itemId) {
        log.info("GetItemById item id {} for user id {}", itemId, userId);
        return itemService.getItemDtoById(itemId, userId);
    }

    /**
     * Поиск вещей по тексту.
     *
     * @param userId Идентификатор пользователя, выполняющего поиск.
     * @param text   Текст для поиска в названии или описании вещей.
     * @param from   Начальный индекс вещи для постраничных результатов (по умолчанию 0).
     * @param size   Максимальное количество вещей на странице (по умолчанию 10).
     * @return Список объектов {@link ItemCreateEditDto}, удовлетворяющих критериям поиска.
     * @throws IllegalArgumentException Если значения from или size меньше 0.
     */
    @GetMapping("/search")
    public List<ItemCreateEditDto> searchItems(@RequestHeader("X-Sharer-User-Id") Long userId,
                                               @RequestParam String text,
                                               @RequestParam(defaultValue = "0") Integer from,
                                               @RequestParam(defaultValue = "10") Integer size) {
        log.info("User: {} search item by string: {}", userId, text);
        checkRequestParamAndThrowException(from, size);
        return itemService.searchByString(text, userId, from, size);
    }

    /**
     * Добавление новой вещи.
     *
     * @param userId  Идентификатор пользователя-владельца вещи.
     * @param itemCreateEditDto Объект {@link ItemCreateEditDto}, описывающий новую вещь.
     * @return Объект {@link ItemCreateEditDto}, представляющий сохраненную вещь.
     */
    @PostMapping
    public ItemCreateEditDto saveItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                                      @Valid @RequestBody ItemCreateEditDto itemCreateEditDto) {
        log.info("Save item name: {}, owner id: {}", itemCreateEditDto.getName(), userId);
        return itemService.saveItem(itemCreateEditDto, userId);
    }

    /**
     * Редактирование вещи.
     *
     * @param userId  Идентификатор пользователя-владельца вещи.
     * @param itemId  Идентификатор вещи, которую нужно отредактировать.
     * @param itemCreateEditDto Объект {@link ItemCreateEditDto}, содержащий поля для редактирования.
     * @return Объект {@link ItemCreateEditDto}, представляющий отредактированную вещь.
     */
    @PatchMapping("/{itemId}")
    public ItemCreateEditDto patchItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                                       @PathVariable Long itemId,
                                       @RequestBody ItemCreateEditDto itemCreateEditDto) {
        log.info("Patch item name: {}, owner id: {}", itemCreateEditDto.getId(), userId);
        return itemService.patchItem(itemCreateEditDto, itemId, userId);
    }

    /**
     * Сохраняет новый комментарий для конкретного item.
     *
     * @param userId              Идентификатор пользователя, полученный из заголовка "X-Sharer-User-Id".
     * @param itemId              Идентификатор item, для которого сохраняется комментарий.
     * @param commentCreateDto    Объект передачи данных (DTO) с информацией для создания комментария.
     *
     * @return                    Объект CommentReadDto, представляющий сохраненный комментарий.
     */
    @PostMapping({"/{itemId}/comment"})
    public CommentReadDto saveComment(@RequestHeader("X-Sharer-User-Id") Long userId,
                                      @PathVariable Long itemId,
                                      @Valid @RequestBody CommentCreateDto commentCreateDto) {
        log.info("Save comment item id");
        return commentService.saveComment(userId, itemId, commentCreateDto);
    }

    private static void checkRequestParamAndThrowException(Integer from, Integer size) {
        if (from < 0 || size < 1) {
            throw new IllegalArgumentException("Request param incorrect");
        }
    }
}
