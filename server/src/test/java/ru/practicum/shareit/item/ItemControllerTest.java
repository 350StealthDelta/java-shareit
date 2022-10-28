package ru.practicum.shareit.item;

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
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoForOut;
import ru.practicum.shareit.item.dto.ItemDtoMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.util.TimeAdapterGsonBuilder;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemController.class)
@AutoConfigureMockMvc
class ItemControllerTest {

    @MockBean
    ItemService service;
    @Autowired
    MockMvc mvc;
    static ItemDtoMapper mapper;
    static Gson gson;
    Item item1;
    Item item2;
    Item item3;
    ItemDto itemDto;
    ItemDtoForOut itemDtoForOut;

    List<Item> items;
    List<ItemDto> itemDtos;
    List<ItemDtoForOut> itemDtoForOuts;

    User user;
    ItemRequest itemRequest;

    @BeforeAll
    static void initial() {
        mapper = new ItemDtoMapper();
        gson = TimeAdapterGsonBuilder.getGson();
    }

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .name("Name")
                .email("user@mail.com")
                .build();

        itemRequest = ItemRequest.builder()
                .id(1L)
                .description("description")
                .requestor(user)
                .created(LocalDateTime.now())
                .build();

        item1 = Item.builder()
                .id(1L)
                .name("item1 name")
                .description("description item1")
                .available(true)
                .owner(user)
                .request(itemRequest)
                .build();
        item2 = Item.builder()
                .id(2L)
                .name("item name2")
                .description("description item2")
                .available(true)
                .owner(user)
                .request(itemRequest)
                .build();
        item3 = Item.builder()
                .id(3L)
                .name("item name3")
                .description("description item3")
                .available(true)
                .owner(user)
                .request(itemRequest)
                .build();

        itemDto = mapper.itemToDto(item1);
        itemDtoForOut = mapper.itemToDtoForOut(item1, null, null, new ArrayList<>());

        items = new ArrayList<>();
        items.add(item1);
        items.add(item2);
        items.add(item3);
        itemDtos = items.stream()
                .map(mapper::itemToDto)
                .collect(Collectors.toList());
        itemDtoForOuts = items.stream()
                .map(i -> mapper.itemToDtoForOut(i, null, null, new ArrayList<>()))
                .collect(Collectors.toList());
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void addNewItem() throws Exception {
        when(service.addItem(any(ItemDto.class), anyLong()))
                .thenReturn(itemDto);

        mvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .content(gson.toJson(itemDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(gson.toJson(itemDto)));

        mvc.perform(post("/items")
                        .content(gson.toJson(itemDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());

/*        itemDto.setName("");
        itemDto.setDescription("");
        mvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .content(gson.toJson(itemDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());*/

        verify(service, times(1))
                .addItem(any(ItemDto.class), anyLong());
    }

    @Test
    void updateItem() throws Exception {
        when(service.updateItem(any(ItemDto.class), anyLong(), anyLong()))
                .thenReturn(itemDto);

        mvc.perform(patch("/items/1")
                        .header("X-Sharer-User-Id", 1L)
                        .content(gson.toJson(itemDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(gson.toJson(itemDto)));

        mvc.perform(patch("/items/1")
                        .content(gson.toJson(itemDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());

/*        itemDto.setAvailable(null);

        mvc.perform(patch("/items/1")
                        .header("X-Sharer-User-Id", 1L)
                        .content(gson.toJson(itemDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());*/

        verify(service, times(1))
                .updateItem(any(ItemDto.class), anyLong(), anyLong());
    }

    @Test
    void getItem() throws Exception {
        when(service.getItem(anyLong(), anyLong()))
                .thenReturn(itemDtoForOut);

        mvc.perform(get("/items/1")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(gson.toJson(itemDtoForOut)));

        mvc.perform(get("/items/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());

        verify(service, times(1))
                .getItem(anyLong(), anyLong());
    }

    @Test
    void getAllItems() throws Exception {
        when(service.getAllItems(anyLong(), any(PageRequest.class)))
                .thenReturn(itemDtoForOuts);

        mvc.perform(get("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(content().json(gson.toJson(itemDtoForOuts)));

        mvc.perform(get("/items")
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isInternalServerError());

        verify(service, times(1))
                .getAllItems(anyLong(), any(PageRequest.class));
    }

    @Test
    void searchItems() throws Exception {
        when(service.searchItems(anyString(), any(PageRequest.class)))
                .thenReturn(itemDtos);

        mvc.perform(get("/items/search")
                        .header("X-Sharer-User-Id", 1L)
                        .param("text", "item1")
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(content().json(gson.toJson(itemDtos)));

        verify(service, times(1))
                .searchItems(anyString(), any(PageRequest.class));
    }

    @Test
    void addComment() throws Exception {
        CommentDto commentDto = CommentDto.builder()
                .id(1L)
                .text("text")
                .itemId(1L)
                .authorName("authorName")
                .created(LocalDateTime.of(2022, 10, 15, 20, 20))
                .build();

        when(service.addComment(anyLong(), anyLong(), any(CommentDto.class)))
                .thenReturn(commentDto);

        mvc.perform(post("/items/1/comment")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(gson.toJson(commentDto)))
                .andExpect(status().isOk())
                .andExpect(content().json(gson.toJson(commentDto)));

        mvc.perform(post("/items/1/comment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(gson.toJson(commentDto)))
                .andExpect(status().isInternalServerError());

/*        commentDto.setText("");

        mvc.perform(post("/items/1/comment")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(gson.toJson(commentDto)))
                .andExpect(status().isBadRequest());*/

        verify(service, times(1))
                .addComment(anyLong(), anyLong(), any(CommentDto.class));
    }
}