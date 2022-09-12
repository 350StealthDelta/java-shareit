package ru.practicum.shareit.item.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class ItemInMemoryRepository implements ItemDao {

    private final Map<Long, Item> items;
    private Long id = 1L;

    @Override
    public Item get(Long itemId) {
        return items.get(itemId);
    }

    @Override
    public List<Item> getAll() {
        return items.entrySet().stream()
                .map(Map.Entry::getValue)
                .collect(Collectors.toList());
    }

    @Override
    public Item create(Item item) {
        item.setId(id++);
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public Item update(Item updateItem, Long itemId) {
        Item item = items.get(itemId);
        if (updateItem.getName() != null) {
            item.setName(updateItem.getName());
        }
        if (updateItem.getDescription() != null) {
            item.setDescription(updateItem.getDescription());
        }
        if (updateItem.getAvailable() != null) {
            item.setAvailable(updateItem.getAvailable());
        }
        if (updateItem.getOwner() != null) {
            item.setOwner(updateItem.getOwner());
        }
        if (updateItem.getAvailable() != null) {
            item.setRequest(updateItem.getRequest());
        }
        return item;
    }

    @Override
    public void delete(Long itemId) {
        items.remove(itemId);
    }

    @Override
    public boolean isItemExist(Long itemId) {
        return items.containsKey(itemId);
    }
}
