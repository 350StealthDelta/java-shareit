package ru.practicum.shareit.request;

import com.google.gson.Gson;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoForOut;
import ru.practicum.shareit.request.dto.ItemRequestDtoMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.util.TimeAdapterGsonBuilder;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemRequestController.class)
@AutoConfigureMockMvc
class ItemRequestControllerTest {

    @MockBean
    ItemRequestService service;
    @Autowired
    MockMvc mvc;

    static ItemRequestDtoMapper mapper;
    static Gson gson;

    ItemRequest itemRequest;
    ItemRequestDto requestDto;
    ItemRequestDtoForOut dtoForOut;
    User user;
    List<ItemDto> itemDtos = new ArrayList<>();

    @BeforeAll
    static void initial() {
        mapper = new ItemRequestDtoMapper();
        gson = TimeAdapterGsonBuilder.getGson();
    }

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .name("User1")
                .email("user1@mail.com")
                .build();

        itemDtos.add(ItemDto.builder()
                .id(1L)
                .name("Нужная вещь")
                .description("Очень нужная всем вещь")
                .available(true)
                .requestId(1L)
                .build());

        itemRequest = ItemRequest.builder()
                .id(1L)
                .description("description")
                .requestor(user)
                .created(LocalDateTime.of(2022, 10, 15, 16, 11))
                .build();

        requestDto = mapper.toRequestDto(itemRequest);

        dtoForOut = mapper.toRequestDtoForOut(itemRequest, itemDtos);
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void addNewRequest() throws Exception {
        when(service.addRequest(any(ItemRequestDto.class), anyLong()))
                .thenReturn(requestDto);

        mvc.perform(post("/requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(gson.toJson(requestDto))
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(content().json(gson.toJson(requestDto)));

        mvc.perform(post("/requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(gson.toJson(requestDto)))
                .andExpect(status().isInternalServerError());

        verify(service, times(1))
                .addRequest(any(ItemRequestDto.class), anyLong());
    }

    @Test
    void getAllOwnRequests() throws Exception {
        List<ItemRequestDtoForOut> dtoForOutList = new ArrayList<>(Collections.singletonList(dtoForOut));

        when(service.getAllOwnRequests(anyLong()))
                .thenReturn(dtoForOutList);

        mvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(gson.toJson(dtoForOutList)));

        mvc.perform(get("/requests")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());

        verify(service, times(1))
                .getAllOwnRequests(anyLong());
    }

    @Test
    void getAllPaging() throws Exception {
        List<ItemRequestDtoForOut> dtoForOutList = new ArrayList<>(Collections.singletonList(dtoForOut));

        when(service.getAllPaging(any(PageRequest.class), anyLong()))
                .thenReturn(dtoForOutList);

        mvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", 1L)
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(content().json(gson.toJson(dtoForOutList)));

        mvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(content().json(gson.toJson(dtoForOutList)));

        mvc.perform(get("/requests/all")
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isInternalServerError());

        verify(service, times(2))
                .getAllPaging(any(PageRequest.class), anyLong());
    }

    @Test
    void getById() throws Exception {
        when(service.getById(anyLong(), anyLong()))
                .thenReturn(dtoForOut);

        mvc.perform(get("/requests/1")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(content().json(gson.toJson(dtoForOut)));

        mvc.perform(get("/requests/1"))
                .andExpect(status().isInternalServerError());

        verify(service, times(1))
                .getById(anyLong(), anyLong());
    }
}