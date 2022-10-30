package ru.practicum.shareit.request.service;

import com.google.gson.Gson;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.exception.RequestNotFoundException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoForOut;
import ru.practicum.shareit.item.dto.ItemDtoMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dao.ItemRequestRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoForOut;
import ru.practicum.shareit.request.dto.ItemRequestDtoMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.util.TimeAdapterGsonBuilder;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

class ItemRequestServiceImplTest {

    ItemRequestService service;

    ItemRequestRepository requestRepository;
    UserRepository userRepository;
    ItemRepository itemRepository;

    ItemRequestDtoMapper mapper;
    ItemDtoMapper itemDtoMapper;
    static Gson gson;

    ItemRequest request;
    ItemRequestDto requestDto;
    ItemRequestDtoForOut requestDtoForOut;
    List<ItemRequest> requestList = new ArrayList<>();
    List<ItemRequestDto> requestDtoList;
    List<ItemRequestDtoForOut> requestDtoForOutList;

    User user;
    List<Item> items = new ArrayList<>();
    List<ItemDto> itemDtos;
    List<ItemDtoForOut> itemDtoForOuts;

    @BeforeAll
    static void initial() {
        gson = TimeAdapterGsonBuilder.getGson();
    }

    @BeforeEach
    void setUp() {
        mapper = new ItemRequestDtoMapper();
        itemDtoMapper = new ItemDtoMapper();
        requestRepository = mock(ItemRequestRepository.class);
        userRepository = mock(UserRepository.class);
        itemRepository = mock(ItemRepository.class);

        service = new ItemRequestServiceImpl(requestRepository,
                userRepository,
                itemRepository,
                mapper,
                itemDtoMapper);

        // Инициируем базовые сущности
        user = User.builder()
                .id(1L)
                .name("User1")
                .email("user1@mail.com")
                .build();

        request = ItemRequest.builder()
                .id(1L)
                .description("description")
                .requestor(user)
                .created(LocalDateTime.of(2022, 10, 15, 20, 25))
                .build();

        // Список запросов
        requestList.add(request);

        // Dto запроса
        requestDto = mapper.toRequestDto(request);

        // Список Dto запросов
        requestDtoList = requestList.stream()
                .map(mapper::toRequestDto).collect(Collectors.toList());

        // Заполняем список items
        items.add(Item.builder()
                .id(1L)
                .name("Нужная вещь")
                .description("Очень нужная вещь")
                .available(true)
                .owner(user)
                .request(request)
                .build());
        items.add(Item.builder()
                .id(2L)
                .name("Нужная вещь 2")
                .description("Очень нужная вещь 2")
                .available(true)
                .owner(user)
                .request(request)
                .build());

        itemDtos = items.stream()
                .map(itemDtoMapper::itemToDto)
                .collect(Collectors.toList());
        itemDtoForOuts = items.stream()
                .map(item -> itemDtoMapper.itemToDtoForOut(item, null, null, new ArrayList<>()))
                .collect(Collectors.toList());

        requestDtoForOut = mapper.toRequestDtoForOut(request, itemDtos);

        requestDtoForOutList = requestList.stream()
                .map(r -> mapper.toRequestDtoForOut(r, itemDtos)).collect(Collectors.toList());
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void addRequest() {
        when(requestRepository.save(any(ItemRequest.class)))
                .thenReturn(request);

        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user));

        ItemRequestDto result = service.addRequest(requestDto, user.getId());

        assertNotNull(result);
        assertEquals(requestDto.getId(), result.getId());
        assertEquals(requestDto.getDescription(), result.getDescription());
        assertEquals(requestDto.getUserId(), result.getUserId());
        assertEquals(requestDto.getCreated(), result.getCreated());

        requestDto.setCreated(null);

        result = service.addRequest(requestDto, user.getId());

        assertNotNull(result.getCreated());

        verify(requestRepository, times(2))
                .save(any(ItemRequest.class));
        verify(userRepository, times(2))
                .findById(anyLong());
    }

    @Test
    void getAllOwnRequests() {
        when(requestRepository.findAllByOwner(anyLong()))
                .thenReturn(requestList);

        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user));

        List<ItemRequestDtoForOut> result = service.getAllOwnRequests(1L);

        assertNotNull(result);
        assertEquals(requestDtoForOutList.size(), result.size());

        verify(requestRepository, times(1))
                .findAllByOwner(anyLong());
        verify(userRepository, times(1))
                .findById(anyLong());
    }

    @Test
    void getAllPaging() {
        when(requestRepository.findAllByRequestorIdIsNot(anyLong(), any(PageRequest.class)))
                .thenReturn(requestList);

        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user));

        List<ItemRequestDtoForOut> result = service.getAllPaging(PageRequest.ofSize(10), 1L);

        assertNotNull(result);
        assertEquals(requestDtoForOutList.size(), result.size());

        verify(requestRepository, times(1))
                .findAllByRequestorIdIsNot(anyLong(), any(PageRequest.class));
        verify(userRepository, times(1))
                .findById(anyLong());
    }

    @Test
    void getById() {
        when(requestRepository.findById(1L))
                .thenReturn(Optional.ofNullable(request));
        when(requestRepository.findById(-1L))
                .thenReturn(Optional.empty());

        when(userRepository.findById(1L))
                .thenReturn(Optional.ofNullable(user));
        when(userRepository.findById(-1L))
                .thenReturn(Optional.empty());

        when(itemRepository.findAllByRequestId(anyLong()))
                .thenReturn(items);

        ItemRequestDtoForOut result = service.getById(1L, 1L);

        assertNotNull(result);
        assertEquals(requestDtoForOut.getId(), result.getId());
        assertEquals(requestDtoForOut.getDescription(), result.getDescription());
        assertEquals(requestDtoForOut.getUserId(), result.getUserId());
        assertEquals(requestDtoForOut.getCreated(), result.getCreated());
        assertEquals(requestDtoForOut.getItems().size(), result.getItems().size());


        assertThrows(UserNotFoundException.class, () -> service.getById(1L, -1L));
        assertThrows(RequestNotFoundException.class, () -> service.getById(-1L, 1L));

        verify(requestRepository, times(2))
                .findById(anyLong());
        verify(userRepository, times(3))
                .findById(anyLong());
        verify(itemRepository, times(1))
                .findAllByRequestId(anyLong());

        // Проверки для покрытия ItemRequest
        ItemRequest testRequest = request = ItemRequest.builder()
                .id(1L)
                .description("description")
                .requestor(user)
                .created(LocalDateTime.of(2022, 10, 15, 20, 25))
                .build();

        assertEquals(request.hashCode(), testRequest.hashCode());
        assertTrue(request.equals(testRequest));
        assertFalse(request.equals(null));
        assertFalse(request.equals(""));
        assertTrue(request.equals(request));
        request.setId(null);
        assertTrue(request.equals(testRequest));

    }
}