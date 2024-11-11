package ru.practicum.shareit.request;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.model.RequestDescriptionObject;
import ru.practicum.shareit.exceptions.NotFoundDataException;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

/**
 * TODO Sprint add-item-requests.
 */
@RestController
@RequestMapping(path = "/requests")
@AllArgsConstructor
public class ItemRequestController {
    ItemRequestService itemRequestService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    ItemRequestDto add(@RequestHeader("X-Sharer-User-Id") long idRequester,
                       @RequestBody RequestDescriptionObject description) throws NotFoundDataException {
        return itemRequestService.add(idRequester, description.getDescription());
    }

    @GetMapping(value = "/all")
    @ResponseStatus(HttpStatus.OK)
    List<ItemRequestDto> getAll(@RequestHeader("X-Sharer-User-Id") long idRequester) {
        return itemRequestService.findAll(idRequester);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    List<ItemRequestDto> getRequesterList(@RequestHeader("X-Sharer-User-Id") long idRequester) {
        return itemRequestService.findByRequester(idRequester);
    }

    @GetMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    ItemRequestDto get(@PathVariable long id) throws NotFoundDataException {
        return itemRequestService.finById(id);
    }
}
