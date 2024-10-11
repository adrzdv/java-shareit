package ru.practicum.shareit.booking;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

/**
 * TODO Sprint add-bookings.
 */
@Builder
@Data
@AllArgsConstructor
public class Booking {
    private long id;
    private LocalDateTime start;
    private LocalDateTime end;
    private Item item;
    private User owner;
    private Status status;
}
