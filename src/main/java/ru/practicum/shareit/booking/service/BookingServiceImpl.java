package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingDao;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingDtoMapper mapper;

    @Override
    @Transactional
    public BookingDto addBooking(BookingDtoInput bookingDtoInput, Long bookerId) {
        if (getUserById(bookerId) == null) {
            throw new WrongOwnerException("Id пользователя null.");
        }
        Booking booking = mapper.toBooking(mapper.toBookongDtoFtomInput(bookingDtoInput),
                getItemById(bookingDtoInput.getItemId()),
                getUserById(bookerId));
        Item item = booking.getItem();
        List<Booking> allByItemOwnerCurrent = bookingDao.findAllByItemOwnerCurrent(
                item.getOwner().getId(),
                bookingDtoInput.getStart(),
                bookingDtoInput.getEnd(),
                null
        );
        if (allByItemOwnerCurrent.size() > 0) {
            item.setAvailable(false);
        }
        if (!item.getAvailable()) {
            throw new ItemNotAvailableForBookingException(String
                    .format("Предмет с id = %s не доступен для бронирования", item.getId()));
        }
        if (item.getOwner().getId().equals(bookerId)) {
            throw new WrongOwnerException(String
                    .format("Пользователь с id = %s является собственником предмета %s", bookerId, item));
        }
        booking.setBooker(getUserById(bookerId));
        booking.setStatus(BookingStatus.WAITING);
        Booking result = bookingDao.save(booking);
        return mapper.toBookingDto(result);
    }

    @Override
    @Transactional
    public BookingDto bookingApprove(Long bookingId, Boolean approve, Long userId) {
        Booking booking = getBookingById(bookingId);
        BookingStatus status = approve ? BookingStatus.APPROVED : BookingStatus.REJECTED;
        if (booking.getStatus() == status) {
            throw new ClashStateException(String
                    .format("Статус %s совпадает со статусом бронирования %s", status, booking));
        }
        if (booking.getItem().getOwner().getId().equals(userId)) {
            booking.setStatus(approve ? BookingStatus.APPROVED : BookingStatus.REJECTED);
        } else {
            throw new WrongOwnerException(String
                    .format("Пользователь с id = %s не является собственником предмета %s",
                            userId, booking.getItem()));
        }
        bookingDao.save(booking);
        return mapper.toBookingDto(booking);
    }

    @Override
    public BookingDto getBooking(Long bookingId, Long userId) {
        Booking booking = getBookingById(bookingId);
        if (booking.getBooker().getId().equals(userId) || booking.getItem().getOwner().getId().equals(userId)) {
            return mapper.toBookingDto(booking);
        } else {
            throw new UserNotFoundException(String
                    .format("Пользователь с id = %s не является собственником предмета или бронирующим.", userId));
        }
    }

    @Override
    public List<BookingDto> getAllUserBookings(BookingState state, Long userId, PageRequest pageRequest) {
        checkUserNotNullAndExist(userId);
        List<BookingDto> result;
        LocalDateTime currentTime = LocalDateTime.now();
        switch (state) {
            case ALL:
                result = bookingDao.findAllByBooker(userId,
                                pageRequest).stream()
                        .map(mapper::toBookingDto)
                        .collect(Collectors.toList());
                break;
            case PAST:
                result = bookingDao.findAllByBookerInPast(userId,
                                currentTime,
                                pageRequest).stream()
                        .map(mapper::toBookingDto)
                        .collect(Collectors.toList());
                break;
            case FUTURE:
                result = bookingDao.findAllByBookerInFuture(userId,
                                currentTime,
                                pageRequest).stream()
                        .map(mapper::toBookingDto)
                        .collect(Collectors.toList());
                break;
            case CURRENT:
                result = bookingDao.findAllByBookerCurrent(userId,
                                currentTime,
                                currentTime,
                                pageRequest).stream()
                        .map(mapper::toBookingDto)
                        .collect(Collectors.toList());
                break;
            case WAITING:
                result = bookingDao.findAllByBookerAndStatus(userId,
                                BookingStatus.WAITING,
                                pageRequest).stream()
                        .map(mapper::toBookingDto)
                        .collect(Collectors.toList());
                break;
            case REJECTED:
                result = bookingDao.findAllByBookerAndStatus(userId,
                                BookingStatus.REJECTED,
                                pageRequest).stream()
                        .map(mapper::toBookingDto)
                        .collect(Collectors.toList());
                break;
            default:
                throw new WrongStateException(String.format("Состояние бронирования %s недопустимо.", state));
        }
        return result;
    }

    @Override
    public List<BookingDto> getAllOwnerItemsBooking(BookingState state, Long userId, PageRequest pageRequest) {
        checkUserNotNullAndExist(userId);
        List<BookingDto> result;
        LocalDateTime currentTime = LocalDateTime.now();
        switch (state) {
            case ALL:
                result = bookingDao.findAllByItemOwner(userId,
                                pageRequest).stream()
                        .map(mapper::toBookingDto)
                        .collect(Collectors.toList());
                break;
            case PAST:
                result = bookingDao.findAllByItemOwnerInPast(userId,
                                currentTime,
                                pageRequest).stream()
                        .map(mapper::toBookingDto)
                        .collect(Collectors.toList());
                break;
            case FUTURE:
                result = bookingDao.findAllByItemOwnerInFuture(userId,
                                currentTime,
                                pageRequest).stream()
                        .map(mapper::toBookingDto)
                        .collect(Collectors.toList());
                break;
            case CURRENT:
                result = bookingDao.findAllByItemOwnerCurrent(userId,
                                currentTime,
                                currentTime,
                                pageRequest).stream()
                        .map(mapper::toBookingDto)
                        .collect(Collectors.toList());
                break;
            case WAITING:
                result = bookingDao.findAllByItemOwnerAndStatus(userId,
                                BookingStatus.WAITING,
                                pageRequest).stream()
                        .map(mapper::toBookingDto)
                        .collect(Collectors.toList());
                break;
            case REJECTED:
                result = bookingDao.findAllByItemOwnerAndStatus(userId,
                                BookingStatus.REJECTED,
                                pageRequest).stream()
                        .map(mapper::toBookingDto)
                        .collect(Collectors.toList());
                break;
            default:
                throw new WrongStateException(String.format("Состояние бронирования %s недопустимо.", state));
        }
        return result;
    }

    private void checkUserNotNullAndExist(Long userId) {
        if (userId != null) {
            getUserById(userId);
        } else {
            throw new UserNotFoundException("Id пользователя - null, поиск бронирований невозможен.");
        }
    }

    private Booking getBookingById(Long bookingId) {
        return bookingDao.findById(bookingId).orElseThrow(() -> {
            throw new BookingNotFoundException(String
                    .format("Бронирование с id = %s не найден.", bookingId));
        });
    }

    private Item getItemById(Long itemId) {
        if (itemId == null) {
            return null;
        }
        return itemRepository.findById(itemId).orElseThrow(() -> new ItemNotFoundException(String
                .format("Предмет с id = %s не найден.", itemId)));
    }

    private User getUserById(Long userId) {
        if (userId == null) {
            return null;
        }
        return userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(String
                .format("Пользователь с id = %s не найден.", userId)));
    }
}
