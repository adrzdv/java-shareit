package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

public class ItemMapper {

    /**
     * Method to transfer item object to item DTO
     *
     * @param item
     * @return ItemDto object
     */
    public static ItemDto toDto(Item item) {

        return ItemDto.builder()
                .name(item.getName())
                .description(item.getDescription())
                .available(String.valueOf(item.isAvailable()))
                .request(item.getRequest() != null ? item.getRequest().getId() : 0)
                .build();
    }

    /**
     * Method to transfer item DTO to item object
     *
     * @param id      identification number
     * @param itemDto itemDto object to transfer
     * @param user    user object (item's owner)
     * @return Item object
     */
    public static Item fromDto(long id, ItemDto itemDto, User user) {

        return Item.builder()
                .id(id)
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .available(Boolean.parseBoolean(itemDto.getAvailable()))
                .owner(user)
                .request(null)
                .build();
    }
}
