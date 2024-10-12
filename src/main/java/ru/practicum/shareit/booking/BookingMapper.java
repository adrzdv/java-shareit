package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDto;

public class BookingMapper {

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
