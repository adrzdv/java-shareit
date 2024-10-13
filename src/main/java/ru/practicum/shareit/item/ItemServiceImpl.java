package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exceptions.NotFoundDataException;
import ru.practicum.shareit.exceptions.NotOwnerException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;

import java.util.List;

@Component
@AllArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemStorage itemStorage;
    private final UserService userService;

    @Override
    public Item add(ItemDto itemDto, long id) throws NotFoundDataException, ValidationException {
        User user = userService.get(id);
        return itemStorage.add(itemDto, user);
    }

    @Override
    public Item update(ItemDto itemDto, long idUser, long idItem) throws NotOwnerException, NotFoundDataException {
        if (!checkOwner(idUser, idItem)) {
            throw new NotOwnerException("Not owner of item");
        }
        return itemStorage.update(idItem, itemDto);
    }

    @Override
    public Item get(long id) throws NotFoundDataException {

        return itemStorage.get(id);
    }

    @Override
    public void delete(long id) throws NotFoundDataException {
        itemStorage.delete(id);
    }

    @Override
    public List<Item> getUserItems(long id) throws NotFoundDataException {
        if (userService.get(id) == null) {
            throw new NotFoundDataException("User not found");
        }
        return itemStorage.getUserItem(id);
    }

    @Override
    public List<ItemDto> search(String searchString) {
        return itemStorage.search(searchString.toLowerCase());
    }

    /**
     * Method for check owner of updating item
     *
     * @param idUser identification number of user
     * @param idItem identification number of item
     * @return boolean true - if it's owner of item
     * @throws NotFoundDataException
     */
    private boolean checkOwner(long idUser, long idItem) throws NotFoundDataException {

        return itemStorage.get(idItem).getOwner().getId() == idUser;
    }
}
