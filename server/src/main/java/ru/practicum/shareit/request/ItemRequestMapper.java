package ru.practicum.shareit.request;

import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.User;

/**
 * Class for transfer ItemRequest object to and from DTO
 */
public class ItemRequestMapper {

    /**
     * Transfer to Dto
     *
     * @param itemRequest ItemRequest object
     * @return ItemRequestDto
     */
    public static ItemRequestDto toDto(ItemRequest itemRequest) {
        return ItemRequestDto.builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .created(itemRequest.getCreated())
                .build();
    }

    /**
     * Transfer from Dto
     *
     * @param itemRequestDto ItemRequestDto object
     * @param requestor      User object of requestor
     * @return ItemRequest object
     */
    public static ItemRequest fromDto(ItemRequestDto itemRequestDto, User requestor) {
        return ItemRequest.builder()
                .id(itemRequestDto.getId())
                .description(itemRequestDto.getDescription())
                .created(itemRequestDto.getCreated())
                .requestor(requestor)
                .build();
    }

}
