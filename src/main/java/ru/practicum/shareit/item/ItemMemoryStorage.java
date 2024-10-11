package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exceptions.NotFoundDataException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
@AllArgsConstructor
public class ItemMemoryStorage implements ItemStorage {
    private final Map<Long, Item> itemMap;

    @Override
    public Item add(ItemDto itemDto, User user) throws NotFoundDataException, ValidationException {
        long key = generateId();
        key++;
        Item newItem = ItemDto.fromDto(key, itemDto, user);
        itemMap.put(newItem.getId(), newItem);

        return get(newItem.getId());
    }

    @Override
    public Item get(long id) throws NotFoundDataException {

        return itemMap.values().stream()
                .filter(item -> item.getId() == id)
                .findFirst()
                .orElseThrow(() -> new NotFoundDataException("Item not found"));
    }

    @Override
    public Item update(long idItem, ItemDto itemDto) throws NotFoundDataException {


        if (itemMap.containsKey(idItem)) {
            Item itemOld = itemMap.get(idItem);

            if (itemDto.getName() != null) {
                itemOld.setName(itemDto.getName());
                itemMap.put(itemOld.getId(), itemOld);
            }
            if (itemDto.getDescription() != null) {
                itemOld.setDescription(itemDto.getDescription());
                itemMap.put(itemOld.getId(), itemOld);
            }
            if (itemDto.getAvailable() != null) {
                itemOld.setAvailable(Boolean.parseBoolean(itemDto.getAvailable()));
                itemMap.put(itemOld.getId(), itemOld);
            }

        } else {
            throw new NotFoundDataException("Item not found");
        }

        return get(idItem);
    }

    @Override
    public List<Item> getUserItem(long id) {
        return itemMap.values().stream()
                .filter(item -> item.getOwner().getId() == id)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(long id) throws NotFoundDataException {
        if (itemMap.containsKey(id)) {
            itemMap.remove(id);
        } else {
            throw new NotFoundDataException("Item not found");
        }
    }

    @Override
    public List<ItemDto> search(String searchString) {

        List<ItemDto> result = new ArrayList<>();
        List<ItemDto> itemsByName = itemMap.values().stream()
                .filter(item -> item.getName().toLowerCase().equals(searchString))
                .filter(Item::isAvailable)
                .map(ItemDto::toDto)
                .toList();
        List<ItemDto> itemsByDescription = itemMap.values().stream()
                .filter(item -> item.getDescription().toLowerCase().equals(searchString))
                .filter(Item::isAvailable)
                .map(ItemDto::toDto)
                .toList();
        if (!itemsByName.isEmpty()) {
            result.addAll(itemsByName);
        }
        if (!itemsByDescription.isEmpty()) {
            result.addAll(itemsByDescription);
        }

        return result;

    }


    /**
     * Method for generating identification number
     *
     * @return long ID
     */
    private long generateId() {

        return itemMap.keySet().stream()
                .max((key1, key2) -> Math.toIntExact(key2 - key1))
                .orElse(1L);
    }
}
