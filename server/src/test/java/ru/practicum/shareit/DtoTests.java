package ru.practicum.shareit;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.AvailabilityStatus;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.comment.CommentMapper;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoResponse;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class DtoTests {
    @Autowired
    private JacksonTester<UserDto> jsonUser;
    @Autowired
    private JacksonTester<BookingDto> jsonBooking;
    @Autowired
    private JacksonTester<BookingDtoResponse> jsonBookingResponse;
    @Autowired
    private JacksonTester<CommentDto> jsonComment;
    @Autowired
    private JacksonTester<Item> jsonItem;
    @Autowired
    private JacksonTester<ItemDto> jsonItemDto;
    @Autowired
    private JacksonTester<ItemDtoResponse> jsonItemResponse;
    @Autowired
    private JacksonTester<ItemRequest> jsonItemRequest;

    private final User user = User.builder()
            .id(1L)
            .name("User")
            .email("example@mail.com")
            .build();

    private final Item item = Item.builder()
            .id(1L)
            .name("Item name")
            .description("Item description")
            .owner(user)
            .available(true)
            .request(null)
            .build();

    @Test
    void testUser() throws Exception {

        UserDto userDto = UserMapper.toDto(user);

        JsonContent<UserDto> result = jsonUser.write(userDto);

        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo(userDto.getName());
        assertThat(result).extractingJsonPathStringValue("$.email").isEqualTo(userDto.getEmail());
    }

    @Test
    void testBooking() throws Exception {
        BookingDto bookingDto = BookingDto.builder()
                .end(LocalDateTime.now().plusHours(2L).plusNanos(999L))
                .start(LocalDateTime.now().plusHours(1L).plusNanos(9999L))
                .status(AvailabilityStatus.WAITING)
                .itemId(1L)
                .booker(1L)
                .build();

        BookingDtoResponse bookingDtoResponse = BookingDtoResponse.builder()
                .id(1L)
                .start(LocalDateTime.now().plusHours(1L).plusNanos(9999L))
                .end(LocalDateTime.now().plusHours(2L))
                .item(item)
                .booker(user)
                .status(AvailabilityStatus.WAITING)
                .build();

        JsonContent<BookingDto> resultBookingDto = jsonBooking.write(bookingDto);
        JsonContent<BookingDtoResponse> resultBookingResponse = jsonBookingResponse.write(bookingDtoResponse);

        assertThat(resultBookingDto).extractingJsonPathNumberValue("$.itemId").isEqualTo(1);
        assertThat(resultBookingDto).extractingJsonPathNumberValue("$.booker").isEqualTo(1);
        assertThat(resultBookingDto).extractingJsonPathStringValue("$.status")
                .isEqualTo(bookingDto.getStatus().name());
        assertThat(resultBookingDto).extractingJsonPathValue("$.start")
                .isEqualTo(bookingDto.getStart().toString());
        assertThat(resultBookingDto).extractingJsonPathValue("$.end")
                .isEqualTo(bookingDto.getEnd().toString());


        assertThat(resultBookingResponse).extractingJsonPathValue("$.id").isEqualTo(1);
        assertThat(resultBookingResponse).extractingJsonPathValue("$.start")
                .isEqualTo(bookingDtoResponse.getStart().toString());
        assertThat(resultBookingResponse).extractingJsonPathValue("$.end")
                .isEqualTo(bookingDtoResponse.getEnd().toString());
        assertThat(resultBookingResponse).extractingJsonPathStringValue("$.status")
                .isEqualTo(bookingDtoResponse.getStatus().name());
        assertThat(resultBookingResponse).extractingJsonPathValue("$.booker.name")
                .isEqualTo(bookingDtoResponse.getBooker().getName());
        assertThat(resultBookingResponse).extractingJsonPathValue("$.booker.email")
                .isEqualTo(bookingDtoResponse.getBooker().getEmail());
        assertThat(resultBookingResponse).extractingJsonPathValue("$.item.name")
                .isEqualTo(bookingDtoResponse.getItem().getName());
        assertThat(resultBookingResponse).extractingJsonPathValue("$.item.description")
                .isEqualTo(bookingDtoResponse.getItem().getDescription());
        assertThat(resultBookingResponse).extractingJsonPathValue("$.item.owner.name")
                .isEqualTo(bookingDtoResponse.getItem().getOwner().getName());
        assertThat(resultBookingResponse).extractingJsonPathValue("$.item.owner.email")
                .isEqualTo(bookingDtoResponse.getItem().getOwner().getEmail());
        assertThat(resultBookingResponse).extractingJsonPathValue("$.item.available")
                .isEqualTo(bookingDtoResponse.getItem().isAvailable());
        assertThat(resultBookingResponse).extractingJsonPathValue("$.item.request")
                .isEqualTo(bookingDtoResponse.getItem().getRequest());
    }

    @Test
    void testComment() throws Exception {
        Comment comment = Comment.builder()
                .id(1L)
                .text("Some text")
                .author(user)
                .item(item)
                .created(LocalDateTime.now().plusNanos(999L))
                .build();

        CommentDto commentDto = CommentMapper.toDto(comment);

        JsonContent<CommentDto> result = jsonComment.write(commentDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.text").isEqualTo(comment.getText());
        assertThat(result).extractingJsonPathStringValue("$.authorName").isEqualTo(comment.getAuthor().getName());
        assertThat(result).extractingJsonPathStringValue("$.itemName").isEqualTo(comment.getItem().getName());
        assertThat(result).extractingJsonPathValue("$.created").isEqualTo(comment.getCreated().toString());

    }

    @Test
    void testItem() throws Exception {
        ItemDto itemDto = ItemMapper.toDto(item);
        ItemDtoResponse itemDtoResponse = ItemMapper.toDtoWithBookingDates(item);

        JsonContent<Item> resultItem = jsonItem.write(item);
        JsonContent<ItemDto> resultItemDto = jsonItemDto.write(itemDto);
        JsonContent<ItemDtoResponse> resultItemResponse = jsonItemResponse.write(itemDtoResponse);

        assertThat(resultItem).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(resultItem).extractingJsonPathStringValue("$.name").isEqualTo(item.getName());
        assertThat(resultItem).extractingJsonPathStringValue("$.description")
                .isEqualTo(item.getDescription());
        assertThat(resultItem).extractingJsonPathBooleanValue("$.available").isEqualTo(item.isAvailable());
        assertThat(resultItem).extractingJsonPathStringValue("$.owner.name")
                .isEqualTo(item.getOwner().getName());
        assertThat(resultItem).extractingJsonPathStringValue("$.owner.email")
                .isEqualTo(item.getOwner().getEmail());
        assertThat(resultItem).extractingJsonPathStringValue("$.request").isEqualTo(item.getRequest());

        assertThat(resultItemDto).extractingJsonPathStringValue("$.name").isEqualTo(item.getName());
        assertThat(resultItemDto).extractingJsonPathStringValue("$.description")
                .isEqualTo(item.getDescription());
        assertThat(resultItemDto).extractingJsonPathStringValue("$.available")
                .isEqualTo(String.valueOf(item.isAvailable()));
        assertThat(resultItemDto).extractingJsonPathNumberValue("$.request").isEqualTo(null);

        assertThat(resultItemResponse).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(resultItemResponse).extractingJsonPathStringValue("$.name").isEqualTo(item.getName());
        assertThat(resultItemResponse).extractingJsonPathStringValue("$.description")
                .isEqualTo(item.getDescription());
        assertThat(resultItemResponse).extractingJsonPathStringValue("$.available")
                .isEqualTo(String.valueOf(item.isAvailable()));
        assertThat(resultItemResponse).extractingJsonPathStringValue("$.owner.name")
                .isEqualTo(item.getOwner().getName());
        assertThat(resultItemResponse).extractingJsonPathStringValue("$.owner.email")
                .isEqualTo(item.getOwner().getEmail());
        assertThat(resultItemResponse).extractingJsonPathValue("$.lastBooking")
                .isEqualTo(itemDtoResponse.getLastBooking());
        assertThat(resultItemResponse).extractingJsonPathValue("$.nextBooking")
                .isEqualTo(itemDtoResponse.getNextBooking());
        assertThat(resultItemResponse).extractingJsonPathArrayValue("$.comments")
                .isEqualTo(itemDtoResponse.getComments());
    }


}
