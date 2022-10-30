package ru.practicum.shareit.item.dao;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {

    @Query(" SELECT i FROM Item AS i " +
            "WHERE upper(i.name) LIKE upper(concat('%', ?1, '%')) " +
            "OR upper(i.description) LIKE upper(concat('%', ?1, '%') ) ")
    List<Item> findAllBySearchParam(String text, PageRequest pageRequest);

    @Query("SELECT i " +
            "FROM Item AS i " +
            "WHERE i.owner.id = ?1 " +
            "ORDER BY i.id")
    List<Item> findAllByOwnerId(Long ownerId, PageRequest pageRequest);

    @Query("SELECT i FROM Item AS i " +
            "WHERE i.request.id = ?1")
    List<Item> findAllByRequestId(Long requestId);
}
