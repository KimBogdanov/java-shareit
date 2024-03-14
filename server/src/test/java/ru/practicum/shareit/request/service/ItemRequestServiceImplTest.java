package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.dto.ItemCreateEditDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.dto.ItemRequestCreatDto;
import ru.practicum.shareit.request.dto.ItemRequestInfoDto;
import ru.practicum.shareit.request.dto.ItemRequestReadDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import java.util.List;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@Transactional
@SpringBootTest(
        properties = "db.name = test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE
)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemRequestServiceImplTest {
    private final UserRepository userRepository;
    private final ItemRequestServiceImpl itemRequestService;
    private final ItemService itemService;
    private final EntityManager em;
    private User user1;
    private User user2;
    private User user3;

    @BeforeEach
    void setUp() {
        User user1 = User.builder()
                .name("John")
                .email("first@mail.com")
                .build();
        User user2 = User.builder()
                .name("Max")
                .email("second@mail.com")
                .build();
        User user3 = User.builder()
                .name("Ivan")
                .email("third@mail.com")
                .build();

        this.user1 = userRepository.save(user1);
        this.user2 = userRepository.save(user2);
        this.user3 = userRepository.save(user3);
    }

    @Test
    @DisplayName("Добавляем ItemRequest")
    void save() {
        ItemRequestCreatDto requestDto = getItemRequestCreatDto("description1");
        itemRequestService.save(requestDto, user1.getId());

        TypedQuery<ItemRequest> query = em.createQuery("Select ir from ItemRequest ir where ir.description = :text",
                ItemRequest.class);
        ItemRequest itemRequest = query.setParameter("text", requestDto.getDescription())
                .getSingleResult();

        assertThat(itemRequest.getId(), notNullValue());
        assertThat(itemRequest.getDescription(), equalTo(requestDto.getDescription()));
        assertThat(itemRequest.getRequester().getId(), equalTo(user1.getId()));
        assertThat(itemRequest.getCreated(), notNullValue());
    }

    @Test
    @DisplayName("Получение itemRequest по requesterId")
    void getItemRequestsByUserId() {
        ItemRequestCreatDto requestDto1 = getItemRequestCreatDto("description1");
        ItemRequestCreatDto requestDto2 = getItemRequestCreatDto("description2");
        ItemRequestReadDto saveDto1 = itemRequestService.save(requestDto1, user1.getId());
        ItemRequestReadDto saveDto2 = itemRequestService.save(requestDto2, user1.getId());
        saveItem(saveDto1, "name1", user2);
        saveItem(saveDto1, "name2", user2);

        List<ItemRequestInfoDto> allRequests = itemRequestService.getAllRequestsByRequesterId(user1.getId());
        ItemRequestInfoDto itemRequestInfoDto = allRequests.get(0);
        ItemRequestInfoDto itemRequestInfoDto1 = allRequests.get(1);

        assertThat(itemRequestInfoDto.getId(), equalTo(saveDto1.getId()));
        assertThat(itemRequestInfoDto.getDescription(), equalTo(saveDto1.getDescription()));
        assertThat(itemRequestInfoDto.getItems().size(), equalTo(2));
        assertThat(itemRequestInfoDto.getCreated(), equalTo(saveDto1.getCreated()));

        assertThat(itemRequestInfoDto1.getId(), equalTo(saveDto2.getId()));
        assertThat(itemRequestInfoDto1.getDescription(), equalTo(saveDto2.getDescription()));
        assertThat(itemRequestInfoDto1.getItems().size(), equalTo(0));
        assertThat(itemRequestInfoDto1.getCreated(), equalTo(saveDto2.getCreated()));
    }

    @Test
    @DisplayName("Получение itemRequests по requesterId, когда нет ItemRequest")
    void getItemRequestsByUserIdWithoutItemRequest() {
        List<ItemRequestInfoDto> allRequests = itemRequestService.getAllRequestsByRequesterId(user1.getId());

        assertThat(allRequests.size(), equalTo(0));
    }

    @Test
    @DisplayName("Получение ItemRequest по id")
    void getItemRequestById() {
        ItemRequestCreatDto requestDto1 = getItemRequestCreatDto("description1");
        ItemRequestCreatDto requestDto2 = getItemRequestCreatDto("description2");
        ItemRequestReadDto saveDto1 = itemRequestService.save(requestDto1, user1.getId());
        itemRequestService.save(requestDto2, user1.getId());
        saveItem(saveDto1, "name1", user2);
        saveItem(saveDto1, "name2", user2);

        ItemRequestInfoDto itemRequestById = itemRequestService.getItemRequestById(user3.getId(), saveDto1.getId());

        assertThat(itemRequestById.getId(), equalTo(saveDto1.getId()));
        assertThat(itemRequestById.getDescription(), equalTo(saveDto1.getDescription()));
        assertThat(itemRequestById.getItems().size(), equalTo(2));
        assertThat(itemRequestById.getCreated(), equalTo(saveDto1.getCreated()));

    }

    private static ItemRequestCreatDto getItemRequestCreatDto(String description) {
        return ItemRequestCreatDto.builder()
                .description(description)
                .build();
    }

    private void saveItem(ItemRequestReadDto saveDto1, String name, User owner) {
        ItemCreateEditDto itemDto = ItemCreateEditDto.builder()
                .name(name)
                .description("description1")
                .available(true)
                .requestId(saveDto1.getId()).build();
        itemService.saveItem(itemDto, owner.getId());
    }
}