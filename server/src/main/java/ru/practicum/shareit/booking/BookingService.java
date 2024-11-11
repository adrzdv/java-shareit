package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.exceptions.NotFoundDataException;
import ru.practicum.shareit.exceptions.NotOwnerException;

import java.util.List;

public interface BookingService {

    /**
     * Add new booking
     *
     * @param bookingDto Booking DTO object
     * @param idUser     User's id
     * @return BookingDtoResponse object
     */
    BookingDtoResponse add(BookingDto bookingDto, long idUser) throws NotFoundDataException;

    /**
     * Approve or reject response
     *
     * @param idBooking Booking id
     * @param idUser    User's id
     * @param approved  updating status APPROVED or REJECTED
     * @return BookingDtoResponse object
     */
    BookingDtoResponse approve(long idBooking, long idUser, boolean approved) throws NotOwnerException, NotFoundDataException;

    /**
     * Get all bookings of items owned by user
     *
     * @param idUser User's id
     * @param state  filtering parameter
     * @return List of BookingDtoResponse object
     */
    List<BookingDtoResponse> getByOwner(long idUser, String state) throws NotFoundDataException;

    /**
     * Get current existing booking
     *
     * @param id Booking id
     * @return BookingDtoResponse object
     */
    BookingDtoResponse get(long id) throws NotFoundDataException;

    /**
     * Get all bookings created by current user
     *
     * @param idUser User's id
     * @param param  filtering parameter (ALL/CURRENT/PAST/FUTURE/WAITING/REJECTED
     * @return List of BookingDtoResponse object
     */
    List<BookingDtoResponse> getByBooker(long idUser, String param);

    /**
     * Delete current booking by id
     *
     * @param id booking id
     */
    void delete(long id);
}
