package ru.practicum.shareit.request.dao;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

public interface ItemRequestRepository extends JpaRepository<ItemRequest, java.lang.Long> {

    @Query("SELECT r FROM ItemRequest r " +
            "WHERE r.requestor.id = ?1 " +
            "ORDER BY r.created DESC ")
    List<ItemRequest> findAllByOwner(Long userId);

    List<ItemRequest> findAllByRequestorIdIsNot(Long userId, PageRequest pageRequest);
}
