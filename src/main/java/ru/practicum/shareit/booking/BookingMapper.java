package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;


public class BookingMapper {

    /**
     * Method to transfer Booking object to BookingDTO object
     *
     * @param booking booking object for transfer
     * @return BookingDTO object
     */
    public static BookingDto toDto(Booking booking) {

        return BookingDto.builder()
                .start(booking.getStart())
                .end(booking.getEnd())
                .itemId(booking.getItem().getId())
                .booker(booking.getBooker().getId())
                .status(booking.getStatus())
                .build();
    }

    /**
     * Method transfer BookingDTO object to Booking object
     *
     * @param bookingDto BookingDTO object for transfer
     * @param item       Item object
     * @param user       User object
     * @return Booking object
     */
    public static Booking fromDto(BookingDto bookingDto, Item item, User user) {

        return Booking.builder()
                .start(bookingDto.getStart())
                .end(bookingDto.getEnd())
                .item(item)
                .booker(user)
                .status(bookingDto.getStatus())
                .build();
    }

    /**
     * Method transfer Booking object to BookingDtoResponse object
     *
     * @param booking Booking object
     * @return BookingDtoResponse object
     */
    public static BookingDtoResponse toDtoResponse(Booking booking) {

        return BookingDtoResponse.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .item(booking.getItem())
                .booker(booking.getBooker())
                .status(booking.getStatus())
                .build();
    }


}
