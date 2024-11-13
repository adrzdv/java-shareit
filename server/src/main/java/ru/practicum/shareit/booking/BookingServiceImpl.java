package ru.practicum.shareit.booking;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.exceptions.BookingException;
import ru.practicum.shareit.exceptions.NotFoundDataException;
import ru.practicum.shareit.exceptions.NotOwnerException;
import ru.practicum.shareit.exceptions.UnavailableItemException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@Component
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final UserService userService;
    private final ItemRepository itemRepository;

    @Override
    public BookingDtoResponse add(BookingDto bookingDto, long idUser) throws NotFoundDataException {

        Optional<User> ownerOptional = Optional.ofNullable(userService.get(idUser));

        User owner;
        Item item;

        if (ownerOptional.isEmpty()) {
            throw new NotFoundDataException("User not found");
        } else {
            owner = ownerOptional.get();
        }

        Optional<Item> itemOptional = itemRepository.findById(bookingDto.getItemId());

        if (itemOptional.isEmpty()) {
            throw new NotFoundDataException("Item not found");
        } else {
            item = itemOptional.get();
            if (!item.isAvailable()) {
                throw new UnavailableItemException("Item is unavailable");
            }
        }

        if (bookingDto.getStart() == null || bookingDto.getEnd() == null) {
            throw new BookingException("Start or end of booking can't be NULL");
        } else if (bookingDto.getEnd().toLocalDate().isBefore(LocalDate.now())) {
            throw new BookingException("End of booking can't be in past");
        } else if (bookingDto.getStart().toLocalDate().isBefore(LocalDate.now())) {
            throw new BookingException("Start of booking can't be in past");
        } else if (bookingDto.getEnd().isEqual(bookingDto.getStart())) {
            throw new BookingException("Start and end of booking can't be equal");
        }


        bookingDto.setStatus(AvailabilityStatus.WAITING);

        return BookingMapper.toDtoResponse(bookingRepository.save(BookingMapper.fromDto(bookingDto, item, owner)));
    }

    @Override
    public BookingDtoResponse approve(long idBooking,
                                      long idUser,
                                      boolean approved) throws NotOwnerException,
            NotFoundDataException {


        if (bookingRepository.getOwnerIdByBookingId(idBooking) != idUser) {
            throw new NotOwnerException("User is not owner");
        }

        if (approved) {
            bookingRepository.setNewStatus(idBooking, AvailabilityStatus.APPROVED.name());
        } else {
            bookingRepository.setNewStatus(idBooking, AvailabilityStatus.REJECTED.name());
        }

        Optional<Booking> bookingOptional = bookingRepository.findById(idBooking);
        if (bookingOptional.isEmpty()) {
            throw new NotFoundDataException("Booking not found");
        }

        return BookingMapper.toDtoResponse(bookingOptional.get());

    }

    @Override
    public List<BookingDtoResponse> getByOwner(long idUser, String state) throws NotFoundDataException {

        if (Optional.ofNullable(userService.get(idUser)).isEmpty()) {
            throw new NotFoundDataException("User not found");
        }

        List<Booking> bookingList = bookingRepository.getBookingsByOwnerId(idUser);

        return getModifiedBookingList(bookingList, state);
    }


    @Override
    public List<BookingDtoResponse> getByBooker(long idUser, String param) {

        List<Booking> bookingList = bookingRepository.findByBooker_Id(idUser);

        return getModifiedBookingList(bookingList, param);
    }

    @Override
    public BookingDtoResponse get(long id) throws NotFoundDataException {

        Optional<Booking> bookingOptional = bookingRepository.findById(id);
        if (bookingOptional.isEmpty()) {
            throw new NotFoundDataException("Booking not found");
        }

        return BookingMapper.toDtoResponse(bookingOptional.get());
    }

    @Override
    public void delete(long id) {
        bookingRepository.deleteById(id);
    }

    /**
     * Method for modifying extracted list of bookings according special filter parameter
     *
     * @param bookingList List of Booking objects
     * @param param       filtering parameter
     * @return List of BookingDtoResponse objects
     */
    private List<BookingDtoResponse> getModifiedBookingList(List<Booking> bookingList, String param) {

        List<BookingDtoResponse> result = new ArrayList<>();

        switch (param.toUpperCase()) {
            case ("ALL"):
                result.addAll(bookingList.stream()
                        .map(BookingMapper::toDtoResponse)
                        .toList());
                break;
            case ("CURRENT"):
                result.addAll(bookingList.stream()
                        .filter(booking -> booking.getStart().isBefore(LocalDateTime.now()) &&
                                booking.getEnd().isAfter(LocalDateTime.now()))
                        .map(BookingMapper::toDtoResponse)
                        .toList());
                break;
            case ("PAST"):
                result.addAll(bookingList.stream()
                        .filter(booking -> booking.getEnd().isBefore(LocalDateTime.now()))
                        .map(BookingMapper::toDtoResponse)
                        .toList());
                break;
            case ("WAITING"):
                result.addAll(bookingList.stream()
                        .filter(booking -> booking.getStatus().equals(AvailabilityStatus.WAITING))
                        .map(BookingMapper::toDtoResponse)
                        .toList());
                break;
            case ("REJECTED"):
                result.addAll(bookingList.stream()
                        .filter(booking -> booking.getStatus().equals(AvailabilityStatus.REJECTED))
                        .map(BookingMapper::toDtoResponse)
                        .toList());
                break;
        }

        return result;
    }
}
