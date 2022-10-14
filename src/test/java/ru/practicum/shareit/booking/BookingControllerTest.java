package ru.practicum.shareit.booking;

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
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoInput;
import ru.practicum.shareit.booking.dto.BookingDtoMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.util.TimeAdapterGsonBuilder;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookingController.class)
@AutoConfigureMockMvc
class BookingControllerTest {

    @MockBean
    BookingService service;
    @Autowired
    MockMvc mvc;

    static BookingDtoMapper mapper;
    static Gson gson;

    Booking booking;
    BookingDto bookingDto;
    BookingDtoInput bookingDtoInput;
    List<BookingDto> bookingDtoList;

    @BeforeAll
    static void beforeAll() {
        mapper = new BookingDtoMapper();
        gson = TimeAdapterGsonBuilder.getGson();
    }

    @BeforeEach
    void setUp() {
        booking = Booking.builder()
                .id(1L)
                .start(LocalDateTime.of(2022, 10, 20, 17, 45))
                .end(LocalDateTime.of(2022, 11, 15, 15, 00))
                .item(Item.builder().id(1L).name("item").build())
                .booker(User.builder().id(1L).build())
                .status(BookingStatus.APPROVED)
                .build();

        bookingDto = mapper.toBookingDto(booking);

        bookingDtoInput = BookingDtoInput.builder()
                .itemId(1L)
                .start(LocalDateTime.of(2022, 10, 20, 17, 45))
                .end(LocalDateTime.of(2022, 11, 15, 15, 00))
                .build();

        bookingDtoList = new ArrayList<>(Collections.singleton(bookingDto));
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void addBooking() throws Exception {
        when(service.addBooking(any(BookingDtoInput.class), anyLong()))
                .thenReturn(bookingDto);

        mvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", "1")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(gson.toJson(bookingDtoInput)))
                .andExpect(status().isOk())
                .andExpect(content().json(gson.toJson(bookingDto)));

        mvc.perform(post("/bookings"))
                .andExpect(status().isInternalServerError());

        bookingDtoInput.setStart(LocalDateTime.of(2022, 10, 10, 17, 45));

        mvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", "1")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(gson.toJson(bookingDtoInput)))
                .andExpect(status().isBadRequest());

        bookingDtoInput.setStart(LocalDateTime.of(2022, 10, 20, 17, 45));
        bookingDtoInput.setEnd(LocalDateTime.of(2022, 10, 19, 17, 45));

        mvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", "1")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(gson.toJson(bookingDtoInput)))
                .andExpect(status().isBadRequest());

        verify(service, times(1))
                .addBooking(any(BookingDtoInput.class), anyLong());
    }

    @Test
    void bookingApprove() throws Exception {
        when(service.bookingApprove(anyLong(), anyBoolean(), anyLong()))
                .thenReturn(bookingDto);

        mvc.perform(patch("/bookings/1")
                        .header("X-Sharer-User-Id", "1")
                        .param("approved", "true"))
                .andExpect(status().isOk())
                .andExpect(content().json(gson.toJson(bookingDto)));

        verify(service, times(1))
                .bookingApprove(anyLong(), anyBoolean(), anyLong());
    }

    @Test
    void getBooking() throws Exception {
        when(service.getBooking(anyLong(), anyLong()))
                .thenReturn(bookingDto);

        mvc.perform(get("/bookings/1")
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isOk())
                .andExpect(content().json(gson.toJson(bookingDto)));

        verify(service, times(1))
                .getBooking(anyLong(), anyLong());
    }

    @Test
    void getAllUserBookings() throws Exception {
        when(service.getAllUserBookings(any(BookingState.class), anyLong(), any(PageRequest.class)))
                .thenReturn(bookingDtoList);

        mvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", "1")
                        .param("state", "ALL")
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(content().json(gson.toJson(bookingDtoList)));

        verify(service, times(1))
                .getAllUserBookings(any(BookingState.class), anyLong(), any(PageRequest.class));
    }

    @Test
    void getAllUserItemsBooking() throws Exception {
        when(service.getAllOwnerItemsBooking(any(BookingState.class), anyLong(), any(PageRequest.class)))
                .thenReturn(bookingDtoList);

        mvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", "1")
                        .param("state", "ALL")
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(content().json(gson.toJson(bookingDtoList)));

        verify(service, times(1))
                .getAllOwnerItemsBooking(any(BookingState.class), anyLong(), any(PageRequest.class));
    }
}