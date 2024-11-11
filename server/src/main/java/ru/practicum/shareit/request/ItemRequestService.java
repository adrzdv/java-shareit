package ru.practicum.shareit.request;

import ru.practicum.shareit.exceptions.NotFoundDataException;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

public interface ItemRequestService {
    /**
     * Add a new request
     *
     * @param idRequester id of request creator
     * @param description description of request
     * @return ItemRequestDto object
     * @throws NotFoundDataException
     */
    ItemRequestDto add(long idRequester, String description) throws NotFoundDataException;

    /**
     * Get current existing request by id
     *
     * @param id id of request
     * @return ItemRequestDto object
     * @throws NotFoundDataException
     */
    ItemRequestDto finById(long id) throws NotFoundDataException;

    /**
     * Get list of all requests, except current user requests
     *
     * @param idRequestor id of creator
     * @return List of ItemRequestDto objects
     */
    List<ItemRequestDto> findAll(long idRequestor);

    /**
     * Get list of current creator's requests
     *
     * @param idRequestor id of creator
     * @return List of ItemRequestDto objects
     */
    List<ItemRequestDto> findByRequester(long idRequestor);
}
