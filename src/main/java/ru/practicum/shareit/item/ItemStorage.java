package ru.practicum.shareit.item;

import ru.practicum.shareit.exceptions.NotFoundDataException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.util.List;

/**
 * Interface of item's storage
 */
public interface ItemStorage {

    /**
     * Add new item
     *
     * @param itemDto ItemDto object
     * @param user    user object (owner)
     * @return Item object
     * @throws NotFoundDataException
     * @throws ValidationException
     */
    Item add(ItemDto itemDto, User user) throws NotFoundDataException, ValidationException;


    /**
     * Update an existing item
     *
     * @param idItem  identification number of item
     * @param itemDto ItemDTO object
     * @return updated Item object
     * @throws NotFoundDataException
     */
    Item update(long idItem, ItemDto itemDto) throws NotFoundDataException;


    /**
     * Get an existing item
     *
     * @param idItem identification number
     * @return Item object
     * @throws NotFoundDataException
     */
    Item get(long idItem) throws NotFoundDataException;


    /**
     * Get items by user
     *
     * @param id user's identification number
     * @return List of Item objects
     */
    List<Item> getUserItem(long id);

    /**
     * Delete an existing item
     *
     * @param id identification number of item
     * @throws NotFoundDataException
     */
    void delete(long id) throws NotFoundDataException;

    /**
     * Search item by response string
     *
     * @param serachString response string
     * @return List of ItemDTO objects
     */
    List<ItemDto> search(String serachString);
}
