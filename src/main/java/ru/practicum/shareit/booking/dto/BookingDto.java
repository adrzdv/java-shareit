package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.Status;

import java.time.LocalDateTime;

/**
 * TODO Sprint add-bookings.
 */
@Data
@AllArgsConstructor
@Builder
public class BookingDto {
    private LocalDateTime start;
    private LocalDateTime end;
    private String item;
    private String owner;
    private Status status;

    public BookingDto toDto(Booking booking) {
        return BookingDto.builder()
                .start(booking.getStart())
                .end(booking.getEnd())
                .item(booking.getItem().getName())
                .owner(booking.getOwner().getName())
                .status(booking.getStatus())
                .build();
    }
}
