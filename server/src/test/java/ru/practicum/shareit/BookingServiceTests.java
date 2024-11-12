package ru.practicum.shareit;

import jakarta.transaction.Transactional;
import net.bytebuddy.utility.RandomString;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import ru.practicum.shareit.booking.AvailabilityStatus;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.exceptions.BookingException;
import ru.practicum.shareit.exceptions.NotFoundDataException;
import ru.practicum.shareit.exceptions.NotOwnerException;
import ru.practicum.shareit.exceptions.UnavailableItemException;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Transactional
@SpringBootTest
@Rollback(value = false)
public class BookingServiceTests {
    @Autowired
    private BookingService bs;
    @Autowired
    private UserService us;
    @Autowired
    private ItemService is;
    private User booker;
    private User owner;
    private BookingDto bookingDto;
    private BookingDtoResponse bookingResponse;
    private Item item;


    @BeforeEach
    void setup() throws Exception {
        User bookerAdd = User.builder()
                .name(RandomString.make(RandomString.DEFAULT_LENGTH))
                .email(RandomString.make(RandomString.DEFAULT_LENGTH) + "@email.com")
                .build();
        User ownerAdd = User.builder()
                .name(RandomString.make(RandomString.DEFAULT_LENGTH))
                .email(RandomString.make(RandomString.DEFAULT_LENGTH) + "@email.com")
                .build();
        booker = us.add(bookerAdd);
        owner = us.add(ownerAdd);

        ItemDto itemDto = ItemDto.builder()
                .name(RandomString.make(RandomString.DEFAULT_LENGTH))
                .description(RandomString.make(RandomString.DEFAULT_LENGTH))
                .available("true")
                .build();

        item = is.add(itemDto, owner.getId());

        bookingDto = BookingDto.builder()
                .start(LocalDateTime.now().plusHours(1L))
                .end(LocalDateTime.now().plusHours(2L))
                .booker(booker.getId())
                .itemId(item.getId())
                .build();

        bookingResponse = bs.add(bookingDto, booker.getId());
    }

    @Test
    void approveTest() throws Exception {

        assertThrows(NotOwnerException.class,
                () -> {
                    bs.approve(bookingResponse.getId(), booker.getId(), false);
                });

        BookingDtoResponse result = bs.approve(bookingResponse.getId(), owner.getId(), false);

        assertThat(result.getStatus(), equalTo(AvailabilityStatus.REJECTED));

        result = bs.approve(bookingResponse.getId(), owner.getId(), true);

        assertThat(result.getStatus(), equalTo(AvailabilityStatus.APPROVED));
    }

    @Test
    void ownerAndBookerGetTest() throws Exception {
        List<BookingDtoResponse> ownerList = bs.getByOwner(owner.getId(), "ALL");
        List<BookingDtoResponse> bookerList = bs.getByBooker(booker.getId(), "ALL");

        assertThat(ownerList.size(), equalTo(1));
        assertThat(bookerList.size(), equalTo(1));

        bs.approve(bookingResponse.getId(), owner.getId(), false);

        ownerList = bs.getByOwner(owner.getId(), "REJECTED");
        assertThat(ownerList.size(), equalTo(1));

        ownerList = bs.getByOwner(owner.getId(), "CURRENT");
        assertThat(ownerList.size(), equalTo(0));
        ownerList = bs.getByOwner(owner.getId(), "PAST");
        assertThat(ownerList.size(), equalTo(0));
        ownerList = bs.getByOwner(owner.getId(), "WAITING");
        assertThat(ownerList.size(), equalTo(0));
    }

    @Test
    void getBooking() throws Exception {

        assertThrows(NotFoundDataException.class,
                () -> {
                    bs.get(999999999L);
                });

        BookingDtoResponse bookingTest = bs.get(bookingResponse.getId());
        assertThat(bookingTest.getId(), equalTo(bookingResponse.getId()));
        assertThat(bookingTest.getStatus(), equalTo(bookingResponse.getStatus()));
        assertThat(bookingTest.getStart(), equalTo(bookingResponse.getStart()));
        assertThat(bookingTest.getEnd(), equalTo(bookingResponse.getEnd()));
        assertThat(bookingTest.getBooker(), equalTo(bookingResponse.getBooker()));
        assertThat(bookingTest.getItem(), equalTo(bookingResponse.getItem()));

    }

    @Test
    void throwBookingExceptions() throws Exception {

        BookingDto booking = BookingDto.builder()
                .start(null)
                .end(null)
                .itemId(item.getId())
                .booker(1L)
                .status(AvailabilityStatus.WAITING)
                .build();

        assertThrows(BookingException.class,
                () -> {
                    bs.add(booking, 1L);
                });
    }

    @Test
    void checkEndInPast() throws Exception {

        BookingDto booking = BookingDto.builder()
                .start(LocalDateTime.now().minusHours(3L))
                .end(null)
                .itemId(item.getId())
                .booker(1L)
                .status(AvailabilityStatus.WAITING)
                .build();
        booking.setEnd(LocalDateTime.now().minusDays(3L));

        assertThrows(BookingException.class,
                () -> {
                    bs.add(booking, 1L);
                });
    }

    @Test
    void checkStartInPast() throws Exception {

        BookingDto booking = BookingDto.builder()
                .start(null)
                .end(null)
                .itemId(item.getId())
                .booker(1L)
                .status(AvailabilityStatus.WAITING)
                .build();

        booking.setStart(LocalDateTime.now().minusDays(2L));

        assertThrows(BookingException.class,
                () -> {
                    bs.add(booking, 1L);
                });
    }

    @Test
    void checkEqualDates() throws Exception {

        BookingDto booking = BookingDto.builder()
                .start(null)
                .end(null)
                .itemId(item.getId())
                .booker(1L)
                .status(AvailabilityStatus.WAITING)
                .build();

        LocalDateTime testDateTime = LocalDateTime.now();
        booking.setStart(testDateTime);
        booking.setEnd(testDateTime);

        assertThrows(BookingException.class,
                () -> {
                    bs.add(booking, 1L);
                });
    }

    @Test
    void bookUnavailableItem() throws Exception {

        BookingDto booking = BookingDto.builder()
                .start(LocalDateTime.now().plusHours(3L))
                .end(LocalDateTime.now().plusHours(4L))
                .itemId(item.getId())
                .booker(1L)
                .status(AvailabilityStatus.WAITING)
                .build();

        item.setAvailable(false);

        assertThrows(UnavailableItemException.class,
                () -> {
                    bs.add(booking, 1L);
                });
    }

    @Test
    void throwUncorrectUser() throws Exception {
        BookingDto booking = BookingDto.builder()
                .start(LocalDateTime.now().plusHours(3L))
                .end(LocalDateTime.now().plusHours(4L))
                .itemId(item.getId())
                .booker(777L)
                .status(AvailabilityStatus.WAITING)
                .build();

        assertThrows(NotFoundDataException.class,
                () -> {
                    bs.add(booking, 77777L);
                });

    }
}
