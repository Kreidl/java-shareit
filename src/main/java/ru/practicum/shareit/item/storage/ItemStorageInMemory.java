package ru.practicum.shareit.item.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;
import java.util.HashMap;

@Repository
@RequiredArgsConstructor
public class ItemStorageInMemory implements ItemStorage {
    private long itemCount = 1L;
    private final HashMap<Long, Item> items;

    @Override
    public Item createItem(Item item) {
        item.setId(itemCount);
        itemCount++;
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public Item updateItem(Item updatedItem) {
        Item item = items.get(updatedItem.getId());
        item.setName(updatedItem.getName());
        item.setDescription(updatedItem.getDescription());
        item.setAvailable(updatedItem.isAvailable());
        return item;
    }

    @Override
    public Item getItemById(long itemId) {
        return items.get(itemId);
    }

    @Override
    public Collection<Item> getUserItems(long ownerId) {
        return items.values().stream()
                .filter(item -> item.getOwner().getId() == ownerId)
                .toList();
    }

    @Override
    public Collection<Item> getSearchItems(String text) {
        String finalText = text.toLowerCase();;
        return items.values().stream()
                .filter(item -> item.getName().toLowerCase().contains(finalText) ||
                        item.getDescription().toLowerCase().contains(finalText))
                .filter(Item::isAvailable)
                .toList();
    }

    @Override
    public void deleteItemById(long itemId) {
        items.remove(itemId);
    }
}
