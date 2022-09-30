package ru.practicum.shareit.item.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Comment;

import java.time.LocalDateTime;
import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    @Query("select b " +
            "from Booking b " +
            "where b.item.id = ?1 " +
            "and b.booker.id = ?2 " +
            "and b.end < ?3")
    List<Booking> findAllByItem_IdAndAuthor_Id(Long itemId, Long userId, LocalDateTime now);

    List<Comment> findAllByItem_Id(Long itemId);

    List<Comment> findAllByAuthor_Id(Long userId);
}
