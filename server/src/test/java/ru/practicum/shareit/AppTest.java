package ru.practicum.shareit;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
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
import ru.practicum.shareit.comment.CommentService;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.exceptions.BookingException;
import ru.practicum.shareit.exceptions.NotFoundDataException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequestMapper;
import ru.practicum.shareit.request.ItemRequestService;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;

import java.time.LocalDateTime;
import java.util.*;


import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.hamcrest.Matchers.*;

@Transactional
@SpringBootTest
@Rollback(value = false)
class AppTest {
    @Autowired
    private ItemService is;
    @Autowired
    private UserService us;
    @Autowired
    private EntityManager em;
    @Autowired
    private ItemRequestService irs;
    @Autowired
    private CommentService cs;
    @Autowired
    private BookingService bs;

    private long userRequestorId;
    private User userItemOwner;
    private User userRequestor;
    private Item itemTest;
    private long bookingId;
    private ItemRequestDto itemRequestDto;
    private long userOwnerId;
    private long idItem;

    @BeforeEach
    void setup() throws NotFoundDataException {

        int randomLength = 20;
        userRequestor = User.builder()
                .name(RandomString.make(RandomString.DEFAULT_LENGTH))
                .email(RandomString.make(RandomString.DEFAULT_LENGTH) + "@example.com")
                .build();

        userItemOwner = User.builder()
                .name(RandomString.make(RandomString.DEFAULT_LENGTH))
                .email(RandomString.make(RandomString.DEFAULT_LENGTH) + "@example.com")
                .build();
        us.add(userRequestor);
        us.add(userItemOwner);
        userRequestorId = userRequestor.getId();
        userOwnerId = userItemOwner.getId();
        itemRequestDto = irs.add(userRequestorId, RandomString.make(randomLength));
        itemTest = Item.builder()
                .name(RandomString.make(RandomString.DEFAULT_LENGTH))
                .description(RandomString.make(RandomString.DEFAULT_LENGTH))
                .owner(us.get(userOwnerId))
                .available(true)
                .request(ItemRequestMapper.fromDto(itemRequestDto, userRequestor))
                .build();
        Item itemFromDb = is.add(ItemMapper.toDto(itemTest), userOwnerId);
        idItem = itemFromDb.getId();
        BookingDtoResponse booking = bs.add(BookingDto.builder()
                        .start(LocalDateTime.now().minusHours(1L))
                        .status(AvailabilityStatus.APPROVED)
                        .itemId(idItem)
                        .end(LocalDateTime.now().minusMinutes(30L))
                        .booker(userRequestorId)
                        .build(),
                userRequestorId);
        bookingId = booking.getId();
    }

    @Test
    void saveNewItemByRequest() throws NotFoundDataException {

        long itemRequestId = itemRequestDto.getId();

        ItemRequestDto itemRequestDtoDbFindId = irs.finById(itemRequestId);
        List<ItemRequestDto> listItemRequestWithoutOwner = irs.findAll(userRequestorId);
        List<ItemRequestDto> listItemRequester = irs.findByRequester(userRequestorId);

        TypedQuery<User> userQuery = em.createQuery("Select u from User u where u.id = :id", User.class);
        User user = userQuery.setParameter("id", userRequestorId).getSingleResult();
        assertThat(user.getId(), equalTo(userRequestorId));
        assertThat(user.getName(), equalTo(userRequestor.getName()));
        assertThat(user.getEmail(), equalTo(userRequestor.getEmail()));

        TypedQuery<ItemRequest> itemRequestQuery = em.createQuery("Select ir from ItemRequest ir where ir.id = :id", ItemRequest.class);
        ItemRequest itemRequest = itemRequestQuery.setParameter("id", itemRequestDto.getId()).getSingleResult();
        assertThat(itemRequest.getId(), equalTo(itemRequestDto.getId()));
        assertThat(itemRequest.getCreated(), equalTo(itemRequestDto.getCreated()));
        assertThat(itemRequest.getDescription(), equalTo(itemRequestDto.getDescription()));

        TypedQuery<Item> itemQuery = em.createQuery("Select i from Item i where i.id = :id", Item.class);
        Item item = itemQuery.setParameter("id", idItem).getSingleResult();
        assertThat(item.getId(), equalTo(idItem));
        assertThat(item.getName(), equalTo(itemTest.getName()));
        assertThat(item.getDescription(), equalTo(itemTest.getDescription()));
        assertThat(item.getOwner(), equalToObject(userItemOwner));
        assertThat(item.getRequest(), equalToObject(ItemRequestMapper.fromDto(itemRequestDto, userRequestor)));

        assertThat(listItemRequester, hasSize(1));
        assertThat(itemRequestDtoDbFindId.getId(), equalTo(itemRequestDto.getId()));
        assertThat(itemRequestDtoDbFindId.getDescription(), equalTo(itemRequestDto.getDescription()));
        assertThat(itemRequestDtoDbFindId.getCreated(), equalTo(itemRequestDto.getCreated()));
        assertThat(listItemRequestWithoutOwner, notNullValue());

    }

    @Test
    void addComment() throws Exception {

        assertThrows(BookingException.class,
                () -> {
                    cs.postComment(idItem, userOwnerId, RandomString.make(RandomString.DEFAULT_LENGTH));
                });

        CommentDto commentDto = cs.postComment(idItem, userRequestorId, RandomString.make(RandomString.DEFAULT_LENGTH));

        assertThat(commentDto.getAuthorName(), equalTo(us.get(userRequestorId).getName()));
        assertThat(commentDto.getItemName(), equalTo(is.get(idItem).getName()));

    }

    @Test
    void deleteItem() throws Exception {

        bs.delete(bookingId);
        is.delete(idItem);

        assertThrows(NotFoundDataException.class,
                () -> {
                    is.get(idItem);
                });

        assertThrows(NotFoundDataException.class,
                () -> {
                    bs.get(bookingId);
                });
    }

}