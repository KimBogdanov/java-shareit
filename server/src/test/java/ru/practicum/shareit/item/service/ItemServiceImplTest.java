//package ru.practicum.shareit.item.service;
//
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.transaction.annotation.Transactional;
//import ru.practicum.shareit.booking.dto.BookingCreateDto;
//import ru.practicum.shareit.booking.dto.BookingReadDto;
//import ru.practicum.shareit.booking.service.BookingService;
//import ru.practicum.shareit.exception.NotFoundException;
//import ru.practicum.shareit.exception.NotOwnedException;
//import ru.practicum.shareit.item.comment.dto.CommentCreateDto;
//import ru.practicum.shareit.item.comment.dto.CommentReadDto;
//import ru.practicum.shareit.item.comment.service.CommentService;
//import ru.practicum.shareit.item.dto.ItemCreateEditDto;
//import ru.practicum.shareit.item.dto.ItemReadDto;
//import ru.practicum.shareit.item.model.Item;
//import ru.practicum.shareit.request.model.ItemRequest;
//import ru.practicum.shareit.request.repository.ItemRequestRepository;
//import ru.practicum.shareit.user.model.User;
//import ru.practicum.shareit.user.repository.UserRepository;
//
//import javax.persistence.EntityManager;
//import javax.persistence.TypedQuery;
//
//import java.time.Duration;
//import java.time.LocalDateTime;
//import java.util.ArrayList;
//import java.util.List;
//
//import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
//import static org.hamcrest.CoreMatchers.notNullValue;
//import static org.hamcrest.CoreMatchers.nullValue;
//import static org.hamcrest.MatcherAssert.assertThat;
//import static org.hamcrest.Matchers.equalTo;
//
//@Slf4j
//@Transactional
//@SpringBootTest(
//        properties = "db.name = test",
//        webEnvironment = SpringBootTest.WebEnvironment.NONE
//)
//@RequiredArgsConstructor(onConstructor_ = @Autowired)
//class ItemServiceImplTest {
//
//    public static final long NOT_FOUND_ID = 999L;
//    private final ItemService itemService;
//    private final EntityManager em;
//    private final BookingService bookingService;
//    private final CommentService commentService;
//    private final UserRepository userRepository;
//    private final ItemRequestRepository itemRequestRepository;
//    private User user1;
//    private User user2;
//    private User user3;
//
//    @BeforeEach
//    void setUp() {
//        User user1 = User.builder()
//                .name("John")
//                .email("first@mail.com")
//                .build();
//        User user2 = User.builder()
//                .name("Max")
//                .email("second@mail.com")
//                .build();
//        User user3 = User.builder()
//                .name("Ivan")
//                .email("third@mail.com")
//                .build();
//
//        this.user1 = userRepository.save(user1);
//        this.user2 = userRepository.save(user2);
//        this.user3 = userRepository.save(user3);
//    }
//
//    @Test
//    @DisplayName("Получение item владельцем")
//    void getItemDtoByIdForOwner() {
//        ItemCreateEditDto itemDto = makeItemCreateEditDto("name", null);
//        Long userId = user1.getId();
//        ItemCreateEditDto saveItem = itemService.saveItem(itemDto, userId);
//        BookingCreateDto bookingCreateDto = BookingCreateDto.builder()
//                .start(LocalDateTime.now().minus(Duration.ofDays(2)))
//                .end(LocalDateTime.now().minus(Duration.ofDays(1)))
//                .itemId(saveItem.getId())
//                .build();
//        BookingReadDto lastBooking = bookingService.saveBooking(user2.getId(), bookingCreateDto);
//        bookingService.approvedBooking(userId, lastBooking.getId(), true);
//
//        BookingCreateDto bookingCreateDto1 = BookingCreateDto.builder()
//                .start(LocalDateTime.now().plus(Duration.ofDays(1)))
//                .end(LocalDateTime.now().plus(Duration.ofDays(2)))
//                .itemId(saveItem.getId())
//                .build();
//        BookingReadDto nextBooking = bookingService.saveBooking(user2.getId(), bookingCreateDto1);
//        bookingService.approvedBooking(userId, nextBooking.getId(), true);
//
//        CommentCreateDto commentCreateDto = CommentCreateDto.builder()
//                .text("Comment").build();
//        CommentReadDto comment = commentService.saveComment(user2.getId(), saveItem.getId(), commentCreateDto);
//        ArrayList<CommentReadDto> list = new ArrayList<>();
//        list.add(comment);
//
//        ItemReadDto itemDtoById = itemService.getItemDtoById(saveItem.getId(), userId);
//        assertThat(itemDtoById.getId(), equalTo(saveItem.getId()));
//        assertThat(itemDtoById.getName(), equalTo(itemDto.getName()));
//        assertThat(itemDtoById.getDescription(), equalTo(itemDto.getDescription()));
//        assertThat(itemDtoById.getLastBooking().getId(), equalTo(lastBooking.getId()));
//        assertThat(itemDtoById.getNextBooking().getId(), equalTo(nextBooking.getId()));
//        assertThat(itemDtoById.getComments(), equalTo(list));
//    }
//
//    @Test
//    @DisplayName("Получение item")
//    void getItemDtoByIdr() {
//        ItemCreateEditDto itemDto = makeItemCreateEditDto("name", null);
//        Long userId = user1.getId();
//        ItemCreateEditDto saveItem = itemService.saveItem(itemDto, userId);
//        BookingCreateDto bookingCreateDto = BookingCreateDto.builder()
//                .start(LocalDateTime.now().minus(Duration.ofDays(2)))
//                .end(LocalDateTime.now().minus(Duration.ofDays(1)))
//                .itemId(saveItem.getId())
//                .build();
//        BookingReadDto lastBooking = bookingService.saveBooking(user2.getId(), bookingCreateDto);
//        bookingService.approvedBooking(userId, lastBooking.getId(), true);
//
//        BookingCreateDto bookingCreateDto1 = BookingCreateDto.builder()
//                .start(LocalDateTime.now().plus(Duration.ofDays(1)))
//                .end(LocalDateTime.now().plus(Duration.ofDays(2)))
//                .itemId(saveItem.getId())
//                .build();
//        BookingReadDto nextBooking = bookingService.saveBooking(user2.getId(), bookingCreateDto1);
//        bookingService.approvedBooking(userId, nextBooking.getId(), true);
//
//        CommentCreateDto commentCreateDto = CommentCreateDto.builder()
//                .text("Comment").build();
//        CommentReadDto comment = commentService.saveComment(user2.getId(), saveItem.getId(), commentCreateDto);
//        ArrayList<CommentReadDto> list = new ArrayList<>();
//        list.add(comment);
//
//        ItemReadDto itemDtoById = itemService.getItemDtoById(saveItem.getId(), user3.getId());
//        assertThat(itemDtoById.getId(), equalTo(saveItem.getId()));
//        assertThat(itemDtoById.getName(), equalTo(itemDto.getName()));
//        assertThat(itemDtoById.getDescription(), equalTo(itemDto.getDescription()));
//        assertThat(itemDtoById.getLastBooking(), nullValue());
//        assertThat(itemDtoById.getNextBooking(), nullValue());
//        assertThat(itemDtoById.getComments(), equalTo(list));
//    }
//
//    @Test
//    @DisplayName("Получаем item")
//    void getItemById() {
//        ItemCreateEditDto itemDto = makeItemCreateEditDto("name", null);
//        Long userId = user1.getId();
//
//        ItemCreateEditDto saveItem = itemService.saveItem(itemDto, userId);
//
//        TypedQuery<Item> query = em.createQuery("Select i from Item i where i.id = :id", Item.class);
//        Item item = query.setParameter("id", saveItem.getId())
//                .getSingleResult();
//
//        assertThat(item.getId(), equalTo(saveItem.getId()));
//        assertThat(item.getName(), equalTo(itemDto.getName()));
//        assertThat(item.getDescription(), equalTo(itemDto.getDescription()));
//        assertThat(item.getAvailable(), equalTo(itemDto.getAvailable()));
//        assertThat(item.getRequest(), nullValue());
//    }
//
//    @Test
//    @DisplayName("Получаем несуществующий item")
//    void getNotFoundItemById() {
//        assertThatThrownBy(() -> itemService.getItemById(NOT_FOUND_ID))
//                .isInstanceOf(NotFoundException.class)
//                .hasMessageContaining("Item not found id: " + NOT_FOUND_ID);
//    }
//
//    @Test
//    @DisplayName("Плучение всех вещей пользователя по userId")
//    void findAllItemsByUserId() {
//        ItemCreateEditDto itemDto = makeItemCreateEditDto("name", null);
//        Long userId = user1.getId();
//        ItemCreateEditDto saveItem = itemService.saveItem(itemDto, userId);
//        BookingCreateDto bookingCreateDto = BookingCreateDto.builder()
//                .start(LocalDateTime.now().minus(Duration.ofDays(2)))
//                .end(LocalDateTime.now().minus(Duration.ofDays(1)))
//                .itemId(saveItem.getId())
//                .build();
//        BookingReadDto lastBooking = bookingService.saveBooking(user2.getId(), bookingCreateDto);
//        bookingService.approvedBooking(userId, lastBooking.getId(), true);
//
//        BookingCreateDto bookingCreateDto1 = BookingCreateDto.builder()
//                .start(LocalDateTime.now().plus(Duration.ofDays(1)))
//                .end(LocalDateTime.now().plus(Duration.ofDays(2)))
//                .itemId(saveItem.getId())
//                .build();
//        BookingReadDto nextBooking = bookingService.saveBooking(user2.getId(), bookingCreateDto1);
//        bookingService.approvedBooking(userId, nextBooking.getId(), true);
//
//        CommentCreateDto commentCreateDto = CommentCreateDto.builder()
//                .text("Comment").build();
//        CommentReadDto comment = commentService.saveComment(user2.getId(), saveItem.getId(), commentCreateDto);
//        ArrayList<CommentReadDto> list = new ArrayList<>();
//        list.add(comment);
//
//        List<ItemReadDto> listItemDto = itemService.findAllItemsByUserId(userId, 0, 10);
//        ItemReadDto itemDtoById = listItemDto.get(0);
//        assertThat(itemDtoById.getId(), equalTo(saveItem.getId()));
//        assertThat(itemDtoById.getName(), equalTo(itemDto.getName()));
//        assertThat(itemDtoById.getDescription(), equalTo(itemDto.getDescription()));
//        assertThat(itemDtoById.getLastBooking().getId(), equalTo(lastBooking.getId()));
//        assertThat(itemDtoById.getNextBooking().getId(), equalTo(nextBooking.getId()));
//        assertThat(itemDtoById.getComments(), equalTo(list));
//    }
//
//    @Test
//    @DisplayName("Сохраняем item без request")
//    void saveItem_ShouldSaveItemWithoutRequest() {
//        ItemCreateEditDto itemDto = makeItemCreateEditDto("name", null);
//        Long userId = user1.getId();
//
//        itemService.saveItem(itemDto, userId);
//
//        TypedQuery<Item> query = em.createQuery("Select i from Item i where i.name = :name", Item.class);
//        Item item = query.setParameter("name", itemDto.getName())
//                .getSingleResult();
//
//        assertThat(item.getId(), notNullValue());
//        assertThat(item.getName(), equalTo(itemDto.getName()));
//        assertThat(item.getDescription(), equalTo(itemDto.getDescription()));
//        assertThat(item.getAvailable(), equalTo(itemDto.getAvailable()));
//        assertThat(item.getRequest(), nullValue());
//    }
//
//    @Test
//    @DisplayName("Сохраняем item с request")
//    void saveItem_ShouldSaveItemWithRequest() {
//        ItemRequest request = itemRequestRepository.save(makeItemRequest(user1, LocalDateTime.now()));
//        ItemCreateEditDto itemDto = makeItemCreateEditDto("name", request.getId());
//        Long userId = user2.getId();
//
//        itemService.saveItem(itemDto, userId);
//
//        TypedQuery<Item> query = em.createQuery("Select i from Item i where i.name = :name", Item.class);
//        Item item = query.setParameter("name", itemDto.getName())
//                .getSingleResult();
//
//        assertThat(item.getId(), notNullValue());
//        assertThat(item.getName(), equalTo(itemDto.getName()));
//        assertThat(item.getDescription(), equalTo(itemDto.getDescription()));
//        assertThat(item.getAvailable(), equalTo(itemDto.getAvailable()));
//        assertThat(item.getRequest(), equalTo(request));
//    }
//
//    private ItemRequest makeItemRequest(User requester, LocalDateTime created) {
//        return ItemRequest.builder()
//                .description("some description")
//                .requester(requester)
//                .created(created)
//                .build();
//    }
//
//    private ItemCreateEditDto makeItemCreateEditDto(String name, Long requestId) {
//        return ItemCreateEditDto.builder()
//                .name(name)
//                .description("description")
//                .available(true)
//                .requestId(requestId)
//                .build();
//
//    }
//
//    @Test
//    @DisplayName("Обновляем item поля name, description, available")
//    void updateItem_patchName_patchDescription_patchAvailable() {
//        ItemCreateEditDto itemDto = makeItemCreateEditDto("name", null);
//        Long userId = user1.getId();
//
//        ItemCreateEditDto saveItem = itemService.saveItem(itemDto, userId);
//
//        itemDto.setName("newName");
//
//        itemService.patchItem(itemDto, saveItem.getId(), userId);
//
//        TypedQuery<Item> query = em.createQuery("Select i from Item i where i.id = :id", Item.class);
//        Item item = query.setParameter("id", saveItem.getId())
//                .getSingleResult();
//
//        assertThat(item.getId(), equalTo(saveItem.getId()));
//        assertThat(item.getName(), equalTo(itemDto.getName()));
//        assertThat(item.getDescription(), equalTo(itemDto.getDescription()));
//        assertThat(item.getAvailable(), equalTo(itemDto.getAvailable()));
//        assertThat(item.getRequest(), nullValue());
//
//        itemDto.setDescription("newDescription");
//
//        itemService.patchItem(itemDto, saveItem.getId(), userId);
//
//        query = em.createQuery("Select i from Item i where i.id = :id", Item.class);
//        item = query.setParameter("id", saveItem.getId())
//                .getSingleResult();
//
//        assertThat(item.getId(), equalTo(saveItem.getId()));
//        assertThat(item.getName(), equalTo(itemDto.getName()));
//        assertThat(item.getDescription(), equalTo(itemDto.getDescription()));
//        assertThat(item.getAvailable(), equalTo(itemDto.getAvailable()));
//        assertThat(item.getRequest(), nullValue());
//
//        itemDto.setAvailable(false);
//
//        itemService.patchItem(itemDto, saveItem.getId(), userId);
//
//        query = em.createQuery("Select i from Item i where i.id = :id", Item.class);
//        item = query.setParameter("id", saveItem.getId())
//                .getSingleResult();
//
//        assertThat(item.getId(), equalTo(saveItem.getId()));
//        assertThat(item.getName(), equalTo(itemDto.getName()));
//        assertThat(item.getDescription(), equalTo(itemDto.getDescription()));
//        assertThat(item.getAvailable(), equalTo(itemDto.getAvailable()));
//        assertThat(item.getRequest(), nullValue());
//    }
//
//    @Test
//    @DisplayName("Обновляем несуществующий item")
//    void updateNotFoundItem() {
//        ItemCreateEditDto itemDto = makeItemCreateEditDto("name", null);
//        Long userId = user1.getId();
//
//        assertThatThrownBy(() -> itemService.patchItem(itemDto, NOT_FOUND_ID, userId))
//                .isInstanceOf(NotFoundException.class)
//                .hasMessageContaining("Item not found id: " + NOT_FOUND_ID);
//    }
//
//    @Test
//    @DisplayName("Обновляем item не принадлежащий user")
//    void updateItemNotOwnedUser() {
//        ItemCreateEditDto itemDto = makeItemCreateEditDto("name", null);
//        Long userId = user1.getId();
//
//        ItemCreateEditDto saveItem = itemService.saveItem(itemDto, userId);
//        itemDto.setName("newName");
//
//        assertThatThrownBy(() -> itemService.patchItem(itemDto, saveItem.getId(), user2.getId()))
//                .isInstanceOf(NotOwnedException.class)
//                .hasMessageContaining("Item id = " + saveItem.getId() +
//                        " does not belong to user userId = " + user2.getId());
//
//    }
//
//    @Test
//    @DisplayName("Поиск вещи по name")
//    void searchByName() {
//        ItemCreateEditDto itemDto = makeItemCreateEditDto("name", null);
//        Long userId = user1.getId();
//        ItemCreateEditDto saveItem = itemService.saveItem(itemDto, userId);
//
//        List<ItemCreateEditDto> listItemDto = itemService.searchByString("name", user2.getId(), 0, 10);
//
//        ItemCreateEditDto findItemDto = listItemDto.get(0);
//
//        assertThat(findItemDto.getId(), equalTo(saveItem.getId()));
//        assertThat(findItemDto.getName(), equalTo(itemDto.getName()));
//        assertThat(findItemDto.getDescription(), equalTo(itemDto.getDescription()));
//        assertThat(findItemDto.getAvailable(), equalTo(itemDto.getAvailable()));
//        assertThat(findItemDto.getRequestId(), nullValue());
//    }
//
//    @Test
//    @DisplayName("Поиск вещи по name")
//    void searchByDescription() {
//        ItemCreateEditDto itemDto = makeItemCreateEditDto("name", null);
//        Long userId = user1.getId();
//        ItemCreateEditDto saveItem = itemService.saveItem(itemDto, userId);
//
//        List<ItemCreateEditDto> listItemDto = itemService.searchByString("description", user2.getId(), 0, 10);
//
//        ItemCreateEditDto findItemDto = listItemDto.get(0);
//
//        assertThat(findItemDto.getId(), equalTo(saveItem.getId()));
//        assertThat(findItemDto.getName(), equalTo(itemDto.getName()));
//        assertThat(findItemDto.getDescription(), equalTo(itemDto.getDescription()));
//        assertThat(findItemDto.getAvailable(), equalTo(itemDto.getAvailable()));
//        assertThat(findItemDto.getRequestId(), nullValue());
//    }
//}