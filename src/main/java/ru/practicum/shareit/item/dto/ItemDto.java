package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

/**
 * TODO Sprint add-controllers.
 */
@AllArgsConstructor
@Builder
@Data
public class ItemDto {
    @NotBlank
    private String name;
    @NotBlank
    private String description;
    @NotBlank
    private String available;
    private long request;


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
                .name(itemDto.name)
                .description(itemDto.description)
                .available(Boolean.parseBoolean(itemDto.getAvailable()))
                .owner(user)
                .request(null)
                .build();
    }
}
