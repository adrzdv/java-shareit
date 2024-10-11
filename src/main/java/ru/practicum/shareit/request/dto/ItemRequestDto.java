package ru.practicum.shareit.request.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.request.ItemRequest;

import java.time.LocalDateTime;

/**
 * TODO Sprint add-item-requests.
 */
@Data
@AllArgsConstructor
@Builder
public class ItemRequestDto {
    private String description;
    private String requesterName;
    private LocalDateTime created;

    public ItemRequestDto toDto(ItemRequest itemRequest) {
        return ItemRequestDto.builder()
                .description(itemRequest.getDescription())
                .requesterName(itemRequest.getRequestor().getName())
                .created(itemRequest.getCreated())
                .build();
    }
}
