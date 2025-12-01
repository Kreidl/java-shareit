package ru.practicum.shareit.item.storage;

import ru.practicum.shareit.item.model.Item;

import java.util.Collection;

public interface ItemStorage {
    Item createItem(Item item);

    Item updateItem(Item updatedItem);

    Item getItemById(long itemId);

    Collection<Item> getUserItems(long ownerId);

    Collection<Item> getSearchItems(String text);

    void deleteItemById(long itemId);
}
