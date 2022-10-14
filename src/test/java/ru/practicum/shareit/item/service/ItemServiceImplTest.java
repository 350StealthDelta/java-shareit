package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.booking.dao.BookingRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.exception.RequestNotFoundException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.exception.WrongOwnerException;
import ru.practicum.shareit.item.dao.CommentRepository;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dao.ItemRequestRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.WARN)
class ItemServiceImplTest {
    ItemService service;
    @Mock
    ItemRepository itemRepository;
    @Mock
    UserRepository userRepository;
    @Mock
    BookingRepository bookingRepository;
    @Mock
    CommentRepository commentRepository;
    @Mock
    ItemRequestRepository requestRepository;

    static ItemDtoMapper mapper;
    static CommentDtoMapper commentDtoMapper;

    Item item;
    ItemDto itemDto;
    ItemDtoForOut itemDtoForOut;
    List<Item> itemList;
    List<ItemDto> itemDtoList;
    List<ItemDtoForOut> itemDtoForOutList;
    User user;
    List<User> userList;
    ItemRequest itemRequest;
    List<ItemRequest> requestList;
    Comment comment;
    CommentDto commentDto;
    List<Comment> commentList;
    List<CommentDto> commentDtoList;

    @BeforeAll
    static void beforeAll() {
        mapper = new ItemDtoMapper();
        commentDtoMapper = new CommentDtoMapper();
    }

    @BeforeEach
    void setUp() {
        service = new ItemServiceImpl(itemRepository,
                userRepository,
                bookingRepository,
                commentRepository,
                requestRepository,
                mapper,
                commentDtoMapper);

        user = User.builder()
                .id(1L)
                .name("User1")
                .email("user1@mail.com")
                .build();

        userList = new ArrayList<>(Collections.singleton(user));

        itemRequest = ItemRequest.builder()
                .id(1L)
                .description("description")
                .requestor(user)
                .created(LocalDateTime.of(2022, 10, 15, 20, 25))
                .build();

        requestList = new ArrayList<>(Collections.singleton(itemRequest));

        item = Item.builder()
                .id(1L)
                .name("Name")
                .description("description")
                .available(true)
                .owner(user)
                .request(itemRequest)
                .build();

        itemDto = mapper.itemToDto(item);


        itemList = new ArrayList<>(Collections.singleton(item));

        itemDtoList = new ArrayList<>(Collections.singleton(itemDto));

        comment = Comment.builder()
                .id(1L)
                .text("text")
                .item(item)
                .author(user)
                .created(LocalDateTime.of(2022, 10, 15, 20, 25))
                .build();

        commentDto = commentDtoMapper.commentToDto(comment);

        commentList = new ArrayList<>(Collections.singleton(comment));
        commentDtoList = commentList.stream()
                .map(commentDtoMapper::commentToDto)
                .collect(Collectors.toList());

        itemDtoForOut = mapper.itemToDtoForOut(item, null, null, commentDtoList);

        itemDtoForOutList = new ArrayList<>(Collections.singleton(itemDtoForOut));
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void getItem() {
        when(itemRepository.findById(1L))
                .thenReturn(Optional.ofNullable(item));
        when(bookingRepository.findAllNextBookingsForItem(anyLong(), any(LocalDateTime.class), anyLong()))
                .thenReturn(new ArrayList<>());
        when(bookingRepository.findAllLastBookingsForItem(anyLong(), any(LocalDateTime.class), anyLong()))
                .thenReturn(new ArrayList<>());

        when(commentRepository.findAllByItem_Id(anyLong()))
                .thenReturn(commentList);

        ItemDtoForOut result = service.getItem(1L, 1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(item.getName(), result.getName());
        assertEquals(item.getDescription(), result.getDescription());
        assertEquals(item.getAvailable(), result.getAvailable());
        assertEquals(item.getRequest().getId(), result.getRequestId());
        assertEquals(commentList.size(), result.getComments().size());

        verify(itemRepository, times(1))
                .findById(anyLong());
        verify(bookingRepository, times(1))
                .findAllNextBookingsForItem(anyLong(), any(LocalDateTime.class), anyLong());
        verify(bookingRepository, times(1))
                .findAllLastBookingsForItem(anyLong(), any(LocalDateTime.class), anyLong());

        // Для 100% покрытия Item.class
        Item testItem = Item.builder()
                .id(1L)
                .name("Name")
                .description("description")
                .available(true)
                .owner(user)
                .request(itemRequest)
                .build();
        assertEquals(item.hashCode(), testItem.hashCode());
        assertTrue(item.equals(testItem));
        assertFalse(item.equals(null));
        item.setId(null);
        assertFalse(item.equals(testItem));
    }

    @Test
    void getAllItems() {
        when(itemRepository.findAllByOwnerId(anyLong(), any(PageRequest.class)))
                .thenReturn(itemList);
        when(itemRepository.findById(1L))
                .thenReturn(Optional.ofNullable(item));
        when(bookingRepository.findAllNextBookingsForItem(anyLong(), any(LocalDateTime.class), anyLong()))
                .thenReturn(new ArrayList<>());
        when(bookingRepository.findAllLastBookingsForItem(anyLong(), any(LocalDateTime.class), anyLong()))
                .thenReturn(new ArrayList<>());

        List<ItemDtoForOut> result = service.getAllItems(1L, PageRequest.ofSize(10));

        assertNotNull(result);
        assertEquals(itemList.size(), result.size());

        verify(itemRepository, times(1))
                .findById(anyLong());
        verify(itemRepository, times(1))
                .findAllByOwnerId(anyLong(), any(PageRequest.class));
        verify(bookingRepository, times(1))
                .findAllNextBookingsForItem(anyLong(), any(LocalDateTime.class), anyLong());
        verify(bookingRepository, times(1))
                .findAllLastBookingsForItem(anyLong(), any(LocalDateTime.class), anyLong());
    }

    @Test
    void addItem() {
        when(itemRepository.save(any(Item.class)))
                .thenReturn(item);
        when(requestRepository.findById(1L))
                .thenReturn(Optional.ofNullable(itemRequest));
        // Для проверки исключения
        when(requestRepository.findById(-1L))
                .thenReturn(Optional.empty());
        when(userRepository.findById(1L))
                .thenReturn(Optional.ofNullable(user));

        ItemDto result = service.addItem(itemDto, 1L);

        assertNotNull(result);
        assertEquals(item.getId(), result.getId());
        assertEquals(item.getName(), result.getName());
        assertEquals(item.getDescription(), result.getDescription());
        assertEquals(item.getAvailable(), result.getAvailable());
        assertEquals(item.getRequest().getId(), result.getRequestId());

        assertThrows(UserNotFoundException.class, () ->
                service.addItem(itemDto, -1L));

        itemDto.setRequestId(-1L);

        assertThrows(RequestNotFoundException.class, () ->
                service.addItem(itemDto, 1L));

        verify(itemRepository, times(1))
                .save(any(Item.class));
        verify(requestRepository, times(3))
                .findById(anyLong());
        verify(userRepository, times(2))
                .findById(anyLong());
    }

    @Test
    void updateItem() {
        User testUser = User.builder()
                .id(2L)
                .build();

        when(itemRepository.save(any(Item.class)))
                .thenReturn(item);
        when(itemRepository.findById(1L))
                .thenReturn(Optional.ofNullable(item));
        // Для проверки исключения
        when(itemRepository.findById(-1L))
                .thenReturn(Optional.empty());
        when(userRepository.findById(1L))
                .thenReturn(Optional.ofNullable(user));
        when(userRepository.findById(2L))
                .thenReturn(Optional.ofNullable(testUser));
        when(requestRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(itemRequest));

        ItemDto result = service.updateItem(itemDto, 1L, 1L);

        assertNotNull(result);

        assertThrows(ItemNotFoundException.class, () ->
                service.updateItem(itemDto, -1L, 1L));


        ItemDto testDto = ItemDto.builder().build();

        result = service.updateItem(testDto, 1L, 1L);

        assertNotNull(result);
        assertEquals(itemDto.getId(), result.getId());
        assertEquals(item.getName(), result.getName());
        assertEquals(item.getDescription(), result.getDescription());
        assertEquals(item.getAvailable(), result.getAvailable());
        assertEquals(item.getRequest().getId(), result.getRequestId());

        assertThrows(WrongOwnerException.class, () ->
                service.updateItem(itemDto, 1L, 2L));

        verify(itemRepository, times(2))
                .save(any(Item.class));
    }

    @Test
    void deleteItem() {
        when(itemRepository.existsById(1L))
                .thenReturn(true);
        when(itemRepository.existsById(-1L))
                .thenReturn(false);

        assertDoesNotThrow(() -> service.deleteItem(1L));
        assertThrows(ItemNotFoundException.class, () ->
                service.deleteItem(-1L));

        verify(itemRepository, times(1))
                .deleteById(anyLong());
    }

    @Test
    void searchItems() {
        when(itemRepository.findAllBySearchParam(anyString(), any(PageRequest.class)))
                .thenReturn(itemList);

        List<ItemDto> result = service.searchItems("text", PageRequest.ofSize(10));

        assertNotNull(result);
        assertEquals(itemDtoList.size(), result.size());

        result = service.searchItems("", PageRequest.ofSize(10));
        assertNotNull(result);
        assertEquals(0, result.size());

        verify(itemRepository, times(1))
                .findAllBySearchParam(anyString(), any(PageRequest.class));
    }

    @Test
    void addComment() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user));
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(item));
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user));
        when(commentRepository.findAllByItem_IdAndAuthor_Id(eq(1L), eq(1L), any(LocalDateTime.class)))
                .thenReturn(new ArrayList<>(Collections.singleton(Booking.builder().build())));
        // Для проверки выброшенного исключения
        when(commentRepository.findAllByItem_IdAndAuthor_Id(eq(-1L), anyLong(), any(LocalDateTime.class)))
                .thenReturn(new ArrayList<>());
        when(commentRepository.save(any(Comment.class)))
                .thenReturn(comment);

        CommentDto result = service.addComment(1L, 1L, commentDto);

        assertNotNull(result);
        assertEquals(commentDto.getId(), result.getId());
        assertEquals(commentDto.getText(), result.getText());
        assertEquals(commentDto.getItemId(), result.getItemId());
        assertEquals(commentDto.getAuthorName(), result.getAuthorName());
        assertEquals(commentDto.getCreated(), result.getCreated());

        assertThrows(IllegalArgumentException.class, () ->
                service.addComment(-1L, 1L, commentDto));

        verify(commentRepository, times(2))
                .findAllByItem_IdAndAuthor_Id(anyLong(), anyLong(), any(LocalDateTime.class));
        verify(commentRepository, times(1))
                .save(any(Comment.class));
    }
}