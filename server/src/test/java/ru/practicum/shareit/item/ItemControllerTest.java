//package ru.practicum.shareit.item;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import lombok.SneakyThrows;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.mockito.Mockito;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.http.MediaType;
//import org.springframework.test.web.servlet.MockMvc;
//import ru.practicum.shareit.item.comment.dto.CommentCreateDto;
//import ru.practicum.shareit.item.comment.dto.CommentReadDto;
//import ru.practicum.shareit.item.comment.service.CommentService;
//import ru.practicum.shareit.item.dto.ItemCreateEditDto;
//import ru.practicum.shareit.item.dto.ItemReadDto;
//import ru.practicum.shareit.item.service.ItemService;
//
//import java.nio.charset.StandardCharsets;
//import java.time.LocalDateTime;
//import java.time.format.DateTimeFormatter;
//import java.util.ArrayList;
//import java.util.List;
//
//import static org.mockito.ArgumentMatchers.*;
//import static org.mockito.Mockito.when;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//@WebMvcTest(ItemController.class)
//@AutoConfigureMockMvc
//class ItemControllerTest {
//    public final String userIdHeader = "X-Sharer-User-Id";
//    private final LocalDateTime created = LocalDateTime.parse("2026-03-12T11:44:51.000000000");
//    private final DateTimeFormatter pattern = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
//    @Autowired
//    ObjectMapper mapper;
//    @MockBean
//    private ItemService itemService;
//    @MockBean
//    private CommentService commentService;
//    @Autowired
//    private MockMvc mockMvc;
//
//    @Test
//    @SneakyThrows
//    @DisplayName("Получение всех item по userId")
//    void getAllItemsByUserId() {
//        Long itemId = 1L;
//        Long userId = 2L;
//        ItemReadDto itemReadDto = getBuild(itemId);
//        List<ItemReadDto> itemReadDtoList = new ArrayList<>();
//        itemReadDtoList.add(itemReadDto);
//
//        when(itemService.findAllItemsByUserId(anyLong(), anyInt(), anyInt()))
//                .thenReturn(itemReadDtoList);
//
//        mockMvc.perform(get("/items")
//                        .header(userIdHeader, userId))
//                .andExpect(jsonPath("$.[0].id").value(itemReadDto.getId()))
//                .andExpect(jsonPath("$.[0].name").value(itemReadDto.getName()))
//                .andExpect(jsonPath("$.[0].description").value(itemReadDto.getDescription()))
//                .andExpect(jsonPath("$.[0].available").value(itemReadDto.isAvailable()))
//                .andExpect(jsonPath("$.[0].lastBooking").value(itemReadDto.getLastBooking()))
//                .andExpect(jsonPath("$.[0].nextBooking").value(itemReadDto.getNextBooking()))
//                .andExpect(jsonPath("$.[0].comments").value(itemReadDto.getComments()));
//
//        Mockito.verify(itemService, Mockito.times(1))
//                .findAllItemsByUserId(anyLong(), anyInt(), anyInt());
//    }
//
//    @Test
//    @SneakyThrows
//    @DisplayName("Получение всех item по userId с некоректными параметрами")
//    void getAllItemsByUserIdWithIncorrectFrom() {
//        Long userId = 2L;
//        int from = -1;
//        int size = 1;
//
//        mockMvc.perform(get("/items")
//                        .header(userIdHeader, userId)
//                        .param("from", Integer.toString(from))
//                        .param("size", Integer.toString(size)))
//                .andExpect(status().isBadRequest())
//                .andExpect(jsonPath("$.error").value("Request param incorrect"))
//                .andExpect(jsonPath("$.description").value("Illegal Argument Exception"));
//
//
//        Mockito.verify(itemService, Mockito.never())
//                .findAllItemsByUserId(anyLong(), anyInt(), anyInt());
//    }
//
//    @Test
//    @SneakyThrows
//    @DisplayName("Получение item по id")
//    void getItemById() {
//        Long itemId = 1L;
//        Long userId = 2L;
//        ItemReadDto itemReadDto = getBuild(itemId);
//
//        when(itemService.getItemDtoById(anyLong(), anyLong()))
//                .thenReturn(itemReadDto);
//
//        mockMvc.perform(get("/items/{itemId}", itemId)
//                        .header(userIdHeader, userId))
//                .andExpect(jsonPath("$.id").value(itemReadDto.getId()))
//                .andExpect(jsonPath("$.name").value(itemReadDto.getName()))
//                .andExpect(jsonPath("$.description").value(itemReadDto.getDescription()))
//                .andExpect(jsonPath("$.available").value(itemReadDto.isAvailable()))
//                .andExpect(jsonPath("$.lastBooking").value(itemReadDto.getLastBooking()))
//                .andExpect(jsonPath("$.nextBooking").value(itemReadDto.getNextBooking()))
//                .andExpect(jsonPath("$.comments").value(itemReadDto.getComments()));
//
//        Mockito.verify(itemService, Mockito.times(1))
//                .getItemDtoById(anyLong(), anyLong());
//    }
//
//    @Test
//    @SneakyThrows
//    @DisplayName("Поиск item по text")
//    void searchItems() {
//        Long itemId = 1L;
//        Long userId = 2L;
//        Long requestId = 3L;
//        String itemName = "item";
//        String description = "description";
//        ItemCreateEditDto itemCreateEditDto = getItemCreateEditDto(itemId, requestId, itemName, description);
//        List<ItemCreateEditDto> itemCreateEditDtoArrayList = new ArrayList<>();
//        itemCreateEditDtoArrayList.add(itemCreateEditDto);
//
//        when(itemService.searchByString(anyString(), anyLong(), anyInt(), anyInt()))
//                .thenReturn(itemCreateEditDtoArrayList);
//
//        mockMvc.perform(get("/items/search")
//                        .header(userIdHeader, userId)
//                        .param("text", "text"))
//                .andExpect(jsonPath("$.[0].id").value(itemCreateEditDto.getId()))
//                .andExpect(jsonPath("$.[0].name").value(itemCreateEditDto.getName()))
//                .andExpect(jsonPath("$.[0].description").value(itemCreateEditDto.getDescription()))
//                .andExpect(jsonPath("$.[0].available").value(itemCreateEditDto.getAvailable()))
//                .andExpect(jsonPath("$.[0].requestId").value(requestId));
//
//        Mockito.verify(itemService, Mockito.times(1))
//                .searchByString(anyString(), anyLong(), anyInt(), anyInt());
//    }
//
//    @Test
//    @SneakyThrows
//    @DisplayName("Получение всех item по userId с некоректными параметрами")
//    void searchItemsWithIncorrectFrom() {
//        Long userId = 2L;
//        int from = -1;
//        int size = 1;
//
//        mockMvc.perform(get("/items/search")
//                        .header(userIdHeader, userId)
//                        .param("text", "text")
//                        .param("from", Integer.toString(from))
//                        .param("size", Integer.toString(size)))
//                .andExpect(status().isBadRequest())
//                .andExpect(jsonPath("$.error").value("Request param incorrect"))
//                .andExpect(jsonPath("$.description").value("Illegal Argument Exception"));
//
//
//        Mockito.verify(itemService, Mockito.never())
//                .searchByString(anyString(), anyLong(), anyInt(), anyInt());
//    }
//
//    @Test
//    @SneakyThrows
//    @DisplayName("Получение всех item по userId без параметра text")
//    void searchItemsWithoutParamText() {
//        Long userId = 2L;
//        int from = -1;
//        int size = 1;
//
//        mockMvc.perform(get("/items/search")
//                        .header(userIdHeader, userId)
//                        .param("from", Integer.toString(from))
//                        .param("size", Integer.toString(size)))
//                .andExpect(status().isBadRequest());
//
//        Mockito.verify(itemService, Mockito.never())
//                .searchByString(anyString(), anyLong(), anyInt(), anyInt());
//    }
//
//    @Test
//    @SneakyThrows
//    @DisplayName("Добавление item")
//    void saveItem() {
//        Long itemId = 1L;
//        Long requestId = 2L;
//        Long userId = 3L;
//        String itemName = "item";
//        String description = "description";
//        ItemCreateEditDto itemCreateEditDto = getItemCreateEditDto(itemId, requestId, itemName, description);
//
//        when(itemService.saveItem(any(ItemCreateEditDto.class), anyLong()))
//                .thenReturn(itemCreateEditDto);
//
//        mockMvc.perform(post("/items")
//                        .characterEncoding(StandardCharsets.UTF_8)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(mapper.writeValueAsString(itemCreateEditDto))
//                        .accept(MediaType.APPLICATION_JSON)
//                        .header(userIdHeader, userId))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.id").value(itemId))
//                .andExpect(jsonPath("$.name").value(itemName))
//                .andExpect(jsonPath("$.description").value(description))
//                .andExpect(jsonPath("$.available").value(itemCreateEditDto.getAvailable()))
//                .andExpect(jsonPath("$.requestId").value(requestId));
//
//        Mockito.verify(itemService, Mockito.times(1))
//                .saveItem(itemCreateEditDto, userId);
//    }
//
//    @Test
//    @SneakyThrows
//    @DisplayName("Добавление item без имени")
//    void saveItemWithoutName() {
//        Long itemId = 1L;
//        Long requestId = 2L;
//        Long userId = 3L;
//        String description = "description";
//        ItemCreateEditDto itemCreateEditDto = getItemCreateEditDto(itemId, requestId, null, description);
//
//        when(itemService.saveItem(any(ItemCreateEditDto.class), anyLong()))
//                .thenReturn(itemCreateEditDto);
//
//        mockMvc.perform(post("/items")
//                        .characterEncoding(StandardCharsets.UTF_8)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(mapper.writeValueAsString(itemCreateEditDto))
//                        .accept(MediaType.APPLICATION_JSON)
//                        .header(userIdHeader, userId))
//                .andExpect(status().isBadRequest())
//                .andExpect(jsonPath("$.error").value("Method Argument Not Valid"));
//
//        Mockito.verify(itemService, Mockito.never())
//                .saveItem(itemCreateEditDto, userId);
//    }
//
//
//    @Test
//    @SneakyThrows
//    @DisplayName("Изменение полей item")
//    void patchItem() {
//        Long itemId = 1L;
//        Long requestId = 2L;
//        Long userId = 3L;
//        String itemName = "item";
//        String description = "description";
//        ItemCreateEditDto itemCreateEditDto = getItemCreateEditDto(itemId, requestId, itemName, description);
//
//        when(itemService.patchItem(any(ItemCreateEditDto.class), anyLong(), anyLong()))
//                .thenReturn(itemCreateEditDto);
//
//        mockMvc.perform(patch("/items/{itemId}", itemId)
//                        .characterEncoding(StandardCharsets.UTF_8)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(mapper.writeValueAsString(itemCreateEditDto))
//                        .accept(MediaType.APPLICATION_JSON)
//                        .header(userIdHeader, userId))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.id").value(itemId))
//                .andExpect(jsonPath("$.name").value(itemName))
//                .andExpect(jsonPath("$.description").value(description))
//                .andExpect(jsonPath("$.available").value(itemCreateEditDto.getAvailable()))
//                .andExpect(jsonPath("$.requestId").value(requestId));
//
//        Mockito.verify(itemService, Mockito.times(1))
//                .patchItem(any(ItemCreateEditDto.class), anyLong(), anyLong());
//    }
//
//    @Test
//    @SneakyThrows
//    @DisplayName("Добавление comment")
//    void saveComment() {
//        Long itemId = 1L;
//        Long commentId = 2L;
//        Long userId = 3L;
//        CommentCreateDto commentCreateDto = CommentCreateDto.builder()
//                .text("text").build();
//        CommentReadDto commentReadDto = CommentReadDto.builder()
//                .id(commentId)
//                .text(commentCreateDto.getText())
//                .authorName("authorName")
//                .created(created)
//                .build();
//
//        when(commentService.saveComment(anyLong(), anyLong(), any(CommentCreateDto.class)))
//                .thenReturn(commentReadDto);
//
//        mockMvc.perform(post("/items/{itemId}/comment", itemId)
//                        .characterEncoding(StandardCharsets.UTF_8)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(mapper.writeValueAsString(commentCreateDto))
//                        .accept(MediaType.APPLICATION_JSON)
//                        .header(userIdHeader, userId))
//                .andExpect(jsonPath("$.id").value(commentReadDto.getId()))
//                .andExpect(jsonPath("$.text").value(commentReadDto.getText()))
//                .andExpect(jsonPath("$.authorName").value(commentReadDto.getAuthorName()))
//                .andExpect(jsonPath("$.created").value(created.format(pattern)));
//
//        Mockito.verify(commentService, Mockito.times(1))
//                .saveComment(userId, itemId, commentCreateDto);
//    }
//
//    @Test
//    @SneakyThrows
//    @DisplayName("Добавление comment без текста")
//    void saveCommentWithoutText() {
//        Long itemId = 1L;
//        Long userId = 3L;
//        CommentCreateDto commentCreateDto = CommentCreateDto.builder()
//                .text(null).build();
//
//        mockMvc.perform(post("/items/{itemId}/comment", itemId)
//                        .characterEncoding(StandardCharsets.UTF_8)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(mapper.writeValueAsString(commentCreateDto))
//                        .accept(MediaType.APPLICATION_JSON)
//                        .header(userIdHeader, userId))
//                .andExpect(status().isBadRequest())
//                .andExpect(jsonPath("$.error").value("Method Argument Not Valid"));
//
//        Mockito.verify(commentService, Mockito.never())
//                .saveComment(userId, itemId, commentCreateDto);
//    }
//
//    private static ItemCreateEditDto getItemCreateEditDto(Long itemId,
//                                                          Long requestId,
//                                                          String itemName,
//                                                          String description) {
//        return ItemCreateEditDto.builder()
//                .id(itemId)
//                .name(itemName)
//                .description(description)
//                .available(true)
//                .requestId(requestId)
//                .build();
//    }
//
//    private static ItemReadDto getBuild(Long itemId) {
//        return ItemReadDto.builder()
//                .id(itemId)
//                .name("name")
//                .description("description")
//                .available(true)
//                .lastBooking(null)
//                .nextBooking(null)
//                .comments(new ArrayList<>())
//                .build();
//    }
//}