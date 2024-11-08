package ru.practicum.shareit.booking;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.exceptions.NotFoundDataException;
import ru.practicum.shareit.exceptions.NotOwnerException;

import java.util.List;

/**
 * TODO Sprint add-bookings.
 */
@RestController
@RequestMapping(path = "/bookings")
@AllArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookingDtoResponse add(@RequestBody @Valid BookingDto bookingDto,
                                  @RequestHeader("X-Sharer-User-Id") long idUser) throws NotFoundDataException {
        return bookingService.add(bookingDto, idUser);
    }

    @PatchMapping(value = "/{idBooking}")
    @ResponseStatus(HttpStatus.OK)
    public BookingDtoResponse approve(@RequestHeader("X-Sharer-User-Id") long idUser,
                                      @PathVariable long idBooking,
                                      @RequestParam(required = true) boolean approved) throws NotOwnerException, NotFoundDataException {
        return bookingService.approve(idBooking, idUser, approved);
    }

    @GetMapping(value = "/{idBooking}")
    @ResponseStatus(HttpStatus.OK)
    public BookingDtoResponse get(@PathVariable long idBooking) throws NotFoundDataException {
        return bookingService.get(idBooking);
    }

    @GetMapping(value = "/owner")
    public List<BookingDtoResponse> getByOwner(@RequestHeader("X-Sharer-User-Id") long idUser,
                                               @RequestParam(required = false, defaultValue = "ALL") String state) throws NotFoundDataException {
        return bookingService.getByOwner(idUser, state);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<BookingDtoResponse> getByBooker(@RequestHeader("X-Sharer-User-Id") long idUser,
                                                @RequestParam(required = false, defaultValue = "ALL") String state) {
        return bookingService.getByBooker(idUser, state);
    }


    @DeleteMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void delete(@PathVariable long id) {
        bookingService.delete(id);
    }
}
