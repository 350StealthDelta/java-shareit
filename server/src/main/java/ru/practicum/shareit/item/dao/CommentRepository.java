package ru.practicum.shareit.item.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Comment;

import java.time.LocalDateTime;
import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    @Query("SELECT b " +
            "FROM Booking b " +
            "WHERE b.item.id = ?1 " +
            "AND b.booker.id = ?2 " +
            "AND b.end < ?3")
    List<Booking> findAllByItem_IdAndAuthor_Id(Long itemId, Long userId, LocalDateTime now);

    List<Comment> findAllByItem_Id(Long itemId);

    List<Comment> findAllByAuthor_Id(Long userId);
}
