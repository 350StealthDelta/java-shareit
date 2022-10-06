package ru.practicum.shareit.booking.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    // Получение списка всех бронирований текущего пользователя
    @Query("SELECT b " +
            "FROM Booking AS b " +
            "WHERE b.booker.id = ?1 " +
            "ORDER BY b.start DESC")
    List<Booking> findAllByBooker(Long userId);

    @Query("SELECT b " +
            "FROM Booking AS b " +
            "WHERE b.booker.id = ?1 " +
            "AND b.start < ?2 " +
            "AND b.end > ?3 " +
            "ORDER BY b.start DESC")
    List<Booking> findAllByBookerCurrent(Long userId, LocalDateTime start, LocalDateTime end);

    @Query("SELECT b " +
            "FROM Booking AS b " +
            "WHERE b.booker.id = ?1 " +
            "AND b.end < ?2 " +
            "ORDER BY b.start DESC")
    List<Booking> findAllByBookerInPast(Long userId, LocalDateTime now);

    @Query("SELECT b " +
            "FROM Booking AS b " +
            "WHERE b.booker.id = ?1 " +
            "AND b.start > ?2 " +
            "ORDER BY b.start DESC")
    List<Booking> findAllByBookerInFuture(Long userId, LocalDateTime now);

    @Query("SELECT b " +
            "FROM Booking AS b " +
            "WHERE b.booker.id = ?1 " +
            "AND b.status = ?2 " +
            "ORDER BY b.start DESC")
    List<Booking> findAllByBookerAndStatus(Long userId, BookingStatus status);

    // Получение списка бронирований для всех вещей текущего пользователя
    @Query("SELECT b " +
            "FROM Booking AS b " +
            "WHERE b.item.owner.id = ?1 " +
            "ORDER BY b.start DESC")
    List<Booking> findAllByItemOwner(Long userId);

    @Query("SELECT b " +
            "FROM Booking AS b " +
            "WHERE b.item.owner.id = ?1 " +
            "AND b.start < ?2 " +
            "AND b.end > ?3 " +
            "ORDER BY b.start DESC")
    List<Booking> findAllByItemOwnerCurrent(Long userId, LocalDateTime start, LocalDateTime end);

    @Query("SELECT b " +
            "FROM Booking AS b " +
            "WHERE b.item.owner.id = ?1 " +
            "AND b.end < ?2 " +
            "ORDER BY b.start DESC")
    List<Booking> findAllByItemOwnerInPast(Long userId, LocalDateTime now);

    @Query("SELECT b " +
            "FROM Booking AS b " +
            "WHERE b.item.owner.id = ?1 " +
            "AND b.start > ?2 " +
            "ORDER BY b.start DESC")
    List<Booking> findAllByItemOwnerInFuture(Long userId, LocalDateTime now);

    @Query("select b " +
            "from Booking as b " +
            "where b.item.owner.id = ?1 " +
            "and b.status = ?2 " +
            "order by b.start desc")
    List<Booking> findAllByItemOwnerAndStatus(Long userId, BookingStatus status);

    // Получение списка бронирований для вещи
    @Query("select b " +
            "from Booking as b " +
            "where b.item.id = ?1 " +
            "and b.start > ?2 " +
            "and b.item.owner.id = ?3 " +
            "order by b.start desc ")
    List<Booking> findAllNextBookingsForItem(Long itemId, LocalDateTime currentTime, Long ownerId);

    @Query("select b " +
            "from Booking as b " +
            "where b.item.id = ?1 " +
            "and b.end < ?2 " +
            "and b.item.owner.id = ?3 " +
            "order by b.end asc ")
    List<Booking> findAllLastBookingsForItem(Long itemId, LocalDateTime currentTime, Long userId);
}
