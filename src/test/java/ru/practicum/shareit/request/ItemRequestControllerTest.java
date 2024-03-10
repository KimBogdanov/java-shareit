package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.dto.ItemRequestCreatDto;
import ru.practicum.shareit.request.dto.ItemRequestReadDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemRequestController.class)
@AutoConfigureMockMvc
class ItemRequestControllerTest {
    public final String userIdHeader = "X-Sharer-User-Id";
    private final LocalDateTime created = LocalDateTime.parse("2026-03-12T11:44:51.000000000");
    private final DateTimeFormatter pattern = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
    @Autowired
    ObjectMapper mapper;
    @MockBean
    private ItemRequestService itemRequestService;
    @Autowired
    private MockMvc mockMvc;

    @Test
    @SneakyThrows
    @DisplayName("Добавление itemRequest")
    void saveItemRequest() {
        Long requestId = 1L;
        Long userId = 2L;
        ItemRequestCreatDto itemRequestCreatDto = ItemRequestCreatDto.builder()
                .description("description").build();
        ItemRequestReadDto itemRequestReadDto = ItemRequestReadDto.builder()
                .id(requestId)
                .description(itemRequestCreatDto.getDescription())
                .created(created)
                .build();

        when(itemRequestService.save(any(), anyLong()))
                .thenReturn(itemRequestReadDto);

        mockMvc.perform(post("/requests")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(itemRequestCreatDto))
                        .accept(MediaType.APPLICATION_JSON)
                        .header(userIdHeader, userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemRequestReadDto.getId()))
                .andExpect(jsonPath("$.description").value(itemRequestReadDto.getDescription()))
                .andExpect(jsonPath("$.created").value(itemRequestReadDto.getCreated().format(pattern)));

        Mockito.verify(itemRequestService, Mockito.times(1))
                .save(any(), anyLong());
    }

    @Test
    void getAllRequestsByRequesterId() {
    }

    @Test
    void getAllRequests() {
    }

    @Test
    void getRequestByRequestId() {
    }
}