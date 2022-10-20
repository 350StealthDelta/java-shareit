package ru.practicum.shareit.booking.service;

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
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoInput;
import ru.practicum.shareit.booking.dto.BookingDtoMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static ru.practicum.shareit.booking.model.BookingState.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.WARN)
class BookingServiceImplTest {

    BookingService service;
    @Mock
    BookingRepository bookingRepository;
    @Mock
    ItemRepository itemRepository;
    @Mock
    UserRepository userRepository;

    static BookingDtoMapper mapper;

    Booking booking;
    Booking booking2;
    BookingDto bookingDto;
    BookingDtoInput bookingDtoInput;

    List<Booking> bookingList;
    List<BookingDto> bookingDtoList;

    Item item1;
    Item item2;
    User user1;
    User user2;

    @BeforeAll
    static void beforeAll() {
        mapper = new BookingDtoMapper();
    }

    @BeforeEach
    void setUp() {
        service = new BookingServiceImpl(bookingRepository,
                itemRepository,
                userRepository,
                mapper);

        user1 = User.builder()
                .id(1L)
                .name("User1")
                .email("user1@mail.com")
                .build();
        user2 = User.builder()
                .id(2L)
                .name("User2")
                .email("user2@mail.com")
                .build();


        ItemRequest itemRequest1 = ItemRequest.builder()
                .id(1L)
                .description("description")
                .requestor(user1)
                .created(LocalDateTime.of(2022, 10, 15, 20, 25))
                .build();
        ItemRequest itemRequest2 = ItemRequest.builder()
                .id(1L)
                .description("description")
                .requestor(user2)
                .created(LocalDateTime.of(2022, 10, 16, 20, 25))
                .build();

        item1 = Item.builder()
                .id(1L)
                .name("Name1")
                .description("description1")
                .available(true)
                .owner(user2)
                .request(itemRequest1)
                .build();
        item2 = Item.builder()
                .id(2L)
                .name("Name2")
                .description("description2")
                .available(true)
                .owner(user1)
                .request(itemRequest2)
                .build();

        booking = Booking.builder()
                .id(1L)
                .start(LocalDateTime.of(2022, 10, 15, 17, 45))
                .end(LocalDateTime.of(2022, 11, 20, 15, 00))
                .item(Item.builder().id(1L).name("item").owner(user2).build())
                .booker(User.builder().id(1L).build())
                .status(BookingStatus.WAITING)
                .build();
        booking2 = Booking.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .item(booking.getItem())
                .booker(booking.getBooker())
                .status(BookingStatus.APPROVED)
                .build();

        bookingDto = mapper.toBookingDto(booking);

        bookingDtoInput = BookingDtoInput.builder()
                .itemId(1L)
                .start(LocalDateTime.of(2022, 10, 15, 17, 45))
                .end(LocalDateTime.of(2022, 11, 20, 15, 00))
                .build();

        bookingList = new ArrayList<>(Collections.singleton(booking));
        bookingDtoList = new ArrayList<>(Collections.singleton(bookingDto));
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void addBooking() {
        when(itemRepository.findById(1L))
                .thenReturn(Optional.ofNullable(item1));
        when(itemRepository.findById(2L))
                .thenReturn(Optional.ofNullable(item2));
        // Для проверки исключения
        when(itemRepository.findById(-1L))
                .thenReturn(Optional.empty());
        when(userRepository.findById(1L))
                .thenReturn(Optional.ofNullable(user1));
        when(userRepository.findById(2L))
                .thenReturn(Optional.ofNullable(user2));
        // Для проверки исключения
        when(userRepository.findById(-1L))
                .thenReturn(Optional.empty());
        when(bookingRepository.findAllByItemOwnerCurrent(eq(1L),
                any(LocalDateTime.class), any(LocalDateTime.class), eq(null)))
                .thenReturn(new ArrayList<>());
        when(bookingRepository.save(any(Booking.class)))
                .thenReturn(booking);

        BookingDto result = service.addBooking(bookingDtoInput, 1L);

        assertNotNull(result);
        assertEquals(bookingDto.getId(), result.getId());
        assertEquals(bookingDto.getStart(), result.getStart());
        assertEquals(bookingDto.getEnd(), result.getEnd());
        assertEquals(bookingDto.getItem(), result.getItem());
        assertEquals(bookingDto.getBooker(), result.getBooker());
        assertEquals(bookingDto.getStatus(), result.getStatus());

        assertThrows(UserNotFoundException.class, () ->
                service.addBooking(bookingDtoInput, -1L));

        // cast ItemNotFoundException
        bookingDtoInput.setItemId(-1L);
        assertThrows(ItemNotFoundException.class, () ->
                service.addBooking(bookingDtoInput, 1L));

        // cast WrongOwnerException
        bookingDtoInput.setItemId(1L);
        assertThrows(WrongOwnerException.class, () ->
                service.addBooking(bookingDtoInput, 2L));

        assertThrows(WrongOwnerException.class, () ->
                service.addBooking(bookingDtoInput, null));

        // cast ItemNotAvailableForBookingException
        when(bookingRepository.findAllByItemOwnerCurrent(eq(2L),
                any(LocalDateTime.class), any(LocalDateTime.class), eq(null)))
                .thenReturn(bookingList);

        assertThrows(ItemNotAvailableForBookingException.class, () ->
                service.addBooking(bookingDtoInput, 1L));

        // cast UserNotFoundException
        when(userRepository.findById(eq(-2L)))
                .thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () ->
                service.addBooking(bookingDtoInput, -2L));

        verify(bookingRepository, times(1))
                .save(any(Booking.class));
        verify(bookingRepository, times(3))
                .findAllByItemOwnerCurrent(anyLong(),
                        any(LocalDateTime.class), any(LocalDateTime.class), eq(null));
        verify(itemRepository, times(4))
                .findById(anyLong());
        verify(userRepository, times(10))
                .findById(anyLong());
    }

    @Test
    void bookingApprove() {
        when(bookingRepository.findById(eq(1L)))
                .thenReturn(Optional.ofNullable(booking));
        // Для проверки исключения
        when(bookingRepository.findById(eq(-1L)))
                .thenReturn(Optional.empty());
        when(bookingRepository.save(any(Booking.class)))
                .thenReturn(booking2);

        BookingDto result = service.bookingApprove(1L, true, 2L);

        BookingDto bookingDtoTest = mapper.toBookingDto(booking2);

        assertNotNull(result);
        assertEquals(bookingDtoTest.getId(), result.getId());
        assertEquals(bookingDtoTest.getStart(), result.getStart());
        assertEquals(bookingDtoTest.getEnd(), result.getEnd());
        assertEquals(bookingDtoTest.getItem(), result.getItem());
        assertEquals(bookingDtoTest.getBooker(), result.getBooker());
        assertEquals(bookingDtoTest.getStatus(), result.getStatus());

        assertThrows(BookingNotFoundException.class, () ->
                service.bookingApprove(-1L, true, 1L));

        assertThrows(WrongOwnerException.class, () ->
                service.bookingApprove(1L, false, 1L));

        booking.setStatus(BookingStatus.APPROVED);
        assertThrows(ClashStateException.class, () ->
                service.bookingApprove(1L, true, 2L));

        verify(bookingRepository, times(1))
                .save(any(Booking.class));
        verify(bookingRepository, times(4))
                .findById(anyLong());
    }

    @Test
    void getBooking() {
        when(bookingRepository.findById(eq(1L)))
                .thenReturn(Optional.ofNullable(booking));

        BookingDto result = service.getBooking(1L, 1L);

        assertNotNull(result);
        assertEquals(mapper.toBookingDto(booking), result);

        result = service.getBooking(1L, 2L);

        assertNotNull(result);
        assertEquals(mapper.toBookingDto(booking), result);

        assertThrows(UserNotFoundException.class, () ->
                service.getBooking(1L, 5L));

        assertEquals(booking, booking2);
        assertEquals(booking.hashCode(), booking2.hashCode());

        verify(bookingRepository, times(3))
                .findById(anyLong());
    }

    @Test
    void getAllUserBookings() {
        PageRequest page = PageRequest.ofSize(10);
        when(userRepository.findById(1L))
                .thenReturn(Optional.ofNullable(user1));

        // ALL
        when(bookingRepository.findAllByBooker(eq(1L), any(PageRequest.class)))
                .thenReturn(bookingList);

        List<BookingDto> result = service.getAllUserBookings(ALL, 1L, page);

        assertNotNull(result);
        assertEquals(bookingDtoList, result);

        // PAST
        when(bookingRepository.findAllByBookerInPast(eq(1L), any(LocalDateTime.class), any(PageRequest.class)))
                .thenReturn(bookingList);

        result = service.getAllUserBookings(PAST, 1L, page);

        assertNotNull(result);
        assertEquals(bookingDtoList, result);

        // FUTURE
        when(bookingRepository.findAllByBookerInFuture(eq(1L), any(LocalDateTime.class), any(PageRequest.class)))
                .thenReturn(bookingList);

        result = service.getAllUserBookings(FUTURE, 1L, page);

        assertNotNull(result);
        assertEquals(bookingDtoList, result);

        // CURRENT
        when(bookingRepository.findAllByBookerCurrent(eq(1L),
                any(LocalDateTime.class), any(LocalDateTime.class), any(PageRequest.class)))
                .thenReturn(bookingList);

        result = service.getAllUserBookings(CURRENT, 1L, page);

        assertNotNull(result);
        assertEquals(bookingDtoList, result);

        // WAITING
        when(bookingRepository.findAllByBookerAndStatus(eq(1L),
                eq(BookingStatus.WAITING), any(PageRequest.class)))
                .thenReturn(bookingList);

        result = service.getAllUserBookings(WAITING, 1L, page);

        assertNotNull(result);
        assertEquals(bookingDtoList, result);

        // REJECTED
        when(bookingRepository.findAllByBookerAndStatus(eq(1L),
                eq(BookingStatus.REJECTED), any(PageRequest.class)))
                .thenReturn(bookingList);

        result = service.getAllUserBookings(REJECTED, 1L, page);

        assertNotNull(result);
        assertEquals(bookingDtoList, result);

    }

    @Test
    void getAllOwnerItemsBooking() {
        PageRequest page = PageRequest.ofSize(10);
        when(userRepository.findById(1L))
                .thenReturn(Optional.ofNullable(user1));

        // ALL
        when(bookingRepository.findAllByItemOwner(eq(1L), any(PageRequest.class)))
                .thenReturn(bookingList);

        List<BookingDto> result = service.getAllOwnerItemsBooking(ALL, 1L, page);

        assertNotNull(result);
        assertEquals(bookingDtoList, result);

        // PAST
        when(bookingRepository.findAllByItemOwnerInPast(eq(1L), any(LocalDateTime.class), any(PageRequest.class)))
                .thenReturn(bookingList);

        result = service.getAllOwnerItemsBooking(PAST, 1L, page);

        assertNotNull(result);
        assertEquals(bookingDtoList, result);

        // FUTURE
        when(bookingRepository.findAllByItemOwnerInFuture(eq(1L), any(LocalDateTime.class), any(PageRequest.class)))
                .thenReturn(bookingList);

        result = service.getAllOwnerItemsBooking(FUTURE, 1L, page);

        assertNotNull(result);
        assertEquals(bookingDtoList, result);

        // CURRENT
        when(bookingRepository.findAllByItemOwnerCurrent(eq(1L),
                any(LocalDateTime.class), any(LocalDateTime.class), any(PageRequest.class)))
                .thenReturn(bookingList);

        result = service.getAllOwnerItemsBooking(CURRENT, 1L, page);

        assertNotNull(result);
        assertEquals(bookingDtoList, result);

        // WAITING
        when(bookingRepository.findAllByItemOwnerAndStatus(eq(1L),
                eq(BookingStatus.WAITING), any(PageRequest.class)))
                .thenReturn(bookingList);

        result = service.getAllOwnerItemsBooking(WAITING, 1L, page);

        assertNotNull(result);
        assertEquals(bookingDtoList, result);

        // REJECTED
        when(bookingRepository.findAllByItemOwnerAndStatus(eq(1L),
                eq(BookingStatus.REJECTED), any(PageRequest.class)))
                .thenReturn(bookingList);

        result = service.getAllOwnerItemsBooking(REJECTED, 1L, page);

        assertNotNull(result);
        assertEquals(bookingDtoList, result);

        // cast IllegalArgumentException
        assertThrows(IllegalArgumentException.class, () ->
                service.getAllOwnerItemsBooking(BookingState.stateFromString("WRONG_STATE"), 1L, page));

        // cast WrongStateException
        when(userRepository.findById(-2L))
                .thenReturn(Optional.ofNullable(user1));
        when(bookingRepository.findAllByItemOwner(eq(-2L), any(PageRequest.class)))
                .thenThrow(new WrongStateException(String
                        .format("Состояние бронирования %s недопустимо.", "WRONG_STATE")));

        assertThrows(WrongStateException.class, () ->
                service.getAllOwnerItemsBooking(ALL, -2L, page));

        // cast UserNotFoundException
        assertThrows(UserNotFoundException.class, () ->
                service.getAllOwnerItemsBooking(ALL, null, page));
    }
}