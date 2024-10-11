package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exceptions.NotFoundDataException;
import ru.practicum.shareit.exceptions.NotOwnerException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping("/items")
@AllArgsConstructor
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public Item add(@RequestBody @Valid ItemDto itemDto,
                    @RequestHeader("X-Sharer-User-Id") long id) throws NotFoundDataException, ValidationException {

        return itemService.add(itemDto, id);
    }

    @PatchMapping(value = "/{idItem}")
    @ResponseStatus(HttpStatus.OK)
    public Item update(@RequestBody ItemDto itemDto,
                       @RequestHeader("X-Sharer-User-Id") long idUser,
                       @PathVariable long idItem) throws NotOwnerException, NotFoundDataException {

        return itemService.update(itemDto, idUser, idItem);
    }

    @GetMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Item get(@PathVariable long id) throws NotFoundDataException {
        return itemService.get(id);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<Item> getUserItems(@RequestHeader("X-Sharer-User-Id") long idUser) throws NotFoundDataException {
        return itemService.getUserItems(idUser);
    }

    @DeleteMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void delete(@PathVariable long id) throws NotFoundDataException {
        itemService.delete(id);
    }

    @GetMapping(value = "/search")
    @ResponseStatus(HttpStatus.OK)
    public List<ItemDto> search(@RequestParam String text) {
        return itemService.search(text);
    }
}
