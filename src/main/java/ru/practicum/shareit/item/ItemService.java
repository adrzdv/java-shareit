package ru.practicum.shareit.item;

import ru.practicum.shareit.exceptions.NotFoundDataException;
import ru.practicum.shareit.exceptions.NotOwnerException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

/**
 * Iterface for items service
 */
public interface ItemService {
    /**
     * Add a new item
     *
     * @param itemDto ItemDTO object for add
     * @param id      identification number of user (owner)
     * @return Item
     * @throws NotFoundDataException
     * @throws ValidationException
     */
    Item add(ItemDto itemDto, long id) throws NotFoundDataException, ValidationException;


    /**
     * Update an existing item
     *
     * @param itemDto ItemDTO object for update
     * @param idUser  identification number of user
     * @param idItem  identification number of item
     * @return updated Item object
     * @throws NotFoundDataException
     * @throws NotOwnerException
     */
    Item update(ItemDto itemDto, long idUser, long idItem) throws NotFoundDataException, NotOwnerException;

    /**
     * Get an existing item by ID
     *
     * @param id identification number of item
     * @return Item object
     * @throws NotFoundDataException
     */
    Item get(long id) throws NotFoundDataException;

    /**
     * Delete an existing item
     *
     * @param id identification number of item
     * @throws NotFoundDataException
     */
    void delete(long id) throws NotFoundDataException;

    /**
     * Get existing items by current owner
     *
     * @param id identification number of owner
     * @return List of Item objects
     * @throws NotFoundDataException
     */
    List<Item> getUserItems(long id) throws NotFoundDataException;

    /**
     * Search exiting items by response text
     *
     * @param searchString response string
     * @return List of ItemDTO objects
     */
    List<ItemDto> search(String searchString);

}
