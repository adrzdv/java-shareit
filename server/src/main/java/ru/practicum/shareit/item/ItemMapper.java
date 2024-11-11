package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoResponse;
import ru.practicum.shareit.item.dto.ItemDtoShort;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
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
                .requestId(item.getRequest() != null ? item.getRequest().getId() : 0)
                .build();
    }

    /**
     * Method to transfer item DTO to item object
     *
     * @param itemDto itemDto object to transfer
     * @param user    user object (item's owner)
     * @return Item object
     */
    public static Item fromDto(ItemDto itemDto, User user) {

        return Item.builder()
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .available(Boolean.parseBoolean(itemDto.getAvailable()))
                .owner(user)
                .request(null)
                .build();
    }

    /**
     * Method to transfer item DTO to item object having request
     *
     * @param itemDto itemDto object to transfer
     * @param user    user object (item's owner)
     * @param request ItemRequest object
     * @return Item object
     */
    public static Item fromDtoWithRequest(ItemDto itemDto, User user, ItemRequest request) {
        return Item.builder()
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .available(Boolean.parseBoolean(itemDto.getAvailable()))
                .owner(user)
                .request(request)
                .build();
    }

    /**
     * Method to transfer ItemDtoWithBookingDates object to Item object
     *
     * @param itemDtoWithBookingDates ItemDtoWithBookingDates object for transfer
     * @param owner                   User object of owner
     * @return Item object
     */
    public static Item fromDtoWithDates(ItemDtoResponse itemDtoWithBookingDates, User owner) {
        return Item.builder()
                .name(itemDtoWithBookingDates.getName())
                .description(itemDtoWithBookingDates.getDescription())
                .available(Boolean.parseBoolean(itemDtoWithBookingDates.getAvailable()))
                .owner(owner)
                .request(null)
                .build();
    }

    /**
     * Method for transfer Item object to ItemDtoWithBookingDates object
     *
     * @param item Item object
     * @return ItemDtoWithBookingDates
     */
    public static ItemDtoResponse toDtoWithBookingDates(Item item) {

        return ItemDtoResponse.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(String.valueOf(item.isAvailable()))
                .owner(item.getOwner())
                .itemRequests(item.getRequest())
                .build();
    }

    /**
     * Method for transfer Item object to ItemDtoShort object
     *
     * @param item Item object
     * @return ItemDtoShort object
     */
    public static ItemDtoShort toDtoShort(Item item) {

        return ItemDtoShort.builder()
                .id(item.getId())
                .name(item.getName())
                .idOwner(item.getOwner().getId())
                .build();
    }
}
