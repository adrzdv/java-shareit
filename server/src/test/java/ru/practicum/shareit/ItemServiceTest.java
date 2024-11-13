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
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.exceptions.NotFoundDataException;
import ru.practicum.shareit.exceptions.NotOwnerException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoResponse;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.equalToObject;
import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest
@Rollback(value = false)
public class ItemServiceTest {

    @Autowired
    private ItemService is;
    @Autowired
    private UserService us;
    @Autowired
    private EntityManager em;
    @Autowired
    private BookingService bs;

    private String searchString;
    private User userItemOwnerSearch;
    private Item itemTestSearch;
    private Item item;

    @BeforeEach
    void setup() throws NotFoundDataException {


        searchString = RandomString.make(RandomString.DEFAULT_LENGTH);
        userItemOwnerSearch = User.builder()
                .name(RandomString.make(RandomString.DEFAULT_LENGTH))
                .email(RandomString.make(RandomString.DEFAULT_LENGTH) + "@example.com")
                .build();
        User userOwner = us.add(userItemOwnerSearch);
        itemTestSearch = Item.builder()
                .name(RandomString.make(RandomString.DEFAULT_LENGTH))
                .description(searchString)
                .owner(userOwner)
                .available(true)
                .request(null)
                .build();
        is.add(ItemMapper.toDto(itemTestSearch), userOwner.getId());
    }

    @Test
    void itemUpdateTest() throws Exception {
        User userItemOwner = User.builder()
                .name(RandomString.make(RandomString.DEFAULT_LENGTH))
                .email(RandomString.make(RandomString.DEFAULT_LENGTH) + "@example.com")
                .build();
        User userOwner = us.add(userItemOwner);
        Item itemTest = Item.builder()
                .name(RandomString.make(RandomString.DEFAULT_LENGTH))
                .description(searchString)
                .owner(userOwner)
                .available(true)
                .request(null)
                .build();

        Item item = is.add(ItemMapper.toDto(itemTest), itemTest.getOwner().getId());

        assertThrows(NotOwnerException.class,
                () -> {
                    is.update(ItemMapper.toDto(item), 2L, item.getId());
                });

        ItemDto itemForUpdate = ItemDto.builder()
                .name("New title for test")
                .build();
        is.update(itemForUpdate, item.getOwner().getId(), item.getId());

        TypedQuery<Item> itemQuery = em.createQuery("Select i from Item i where i.id = :id", Item.class);
        Item iq = itemQuery.setParameter("id", item.getId()).getSingleResult();

        assertThat(iq.getId(), equalTo(item.getId()));
        assertThat(iq.getName(), equalTo(itemForUpdate.getName()));
        assertThat(iq.getDescription(), equalTo(itemTest.getDescription()));
        assertThat(iq.getOwner(), equalToObject(userItemOwner));

        ItemDto itemForAnotherUpdate = ItemDto.builder()
                .description("New description for test")
                .build();
        is.update(itemForAnotherUpdate, item.getOwner().getId(), item.getId());

        TypedQuery<Item> itemQueryAnother = em.createQuery("Select i from Item i where i.id = :id", Item.class);
        Item iqa = itemQueryAnother.setParameter("id", item.getId()).getSingleResult();

        assertThat(iqa.getId(), equalTo(item.getId()));
        assertThat(iqa.getName(), equalTo(item.getName()));
        assertThat(iqa.getDescription(), equalTo(itemForAnotherUpdate.getDescription()));
        assertThat(iqa.getOwner(), equalToObject(userItemOwner));

    }

    @Test
    void getItemsTest() throws Exception {
        User userItemOwner = User.builder()
                .name(RandomString.make(RandomString.DEFAULT_LENGTH))
                .email(RandomString.make(RandomString.DEFAULT_LENGTH) + "@example.com")
                .build();
        User userOwner = us.add(userItemOwner);
        Item itemTest = Item.builder()
                .name(RandomString.make(RandomString.DEFAULT_LENGTH))
                .description(searchString)
                .owner(userOwner)
                .available(true)
                .request(null)
                .build();

        item = is.add(ItemMapper.toDto(itemTest), itemTest.getOwner().getId());

        BookingDto booking = BookingDto.builder()
                .itemId(item.getId())
                .booker(userOwner.getId())
                .start(LocalDateTime.now().plusHours(1L))
                .end(LocalDateTime.now().plusHours(2L))
                .build();

        bs.add(booking, userOwner.getId());

        ItemDtoResponse testItemObject = is.get(item.getId());
        List<ItemDtoResponse> itemDtoList = is.getUserItems(userOwner.getId());

        assertThat(testItemObject.getId(), equalTo(item.getId()));
        assertThat(testItemObject.getNextBooking(), equalTo(booking.getStart()));
        assertThat(itemDtoList.size(), equalTo(1));

    }

    @Test
    void searchTest() throws Exception {
        List<ItemDto> list = is.search(searchString);
        assertThat(list.size(), equalTo(0));
    }

    @Test
    void checkGetLastAndLateDate() throws Exception {

        List<ItemDtoResponse> list = is.getUserItems(userItemOwnerSearch.getId());
        assertFalse(list.isEmpty());

    }

    @Test
    void getUnrealUserItems() throws Exception {

        assertThrows(NotFoundDataException.class,
                () -> {
                    is.getUserItems(9999999L);
                });

    }

    @Test
    void updateUnrealItem() throws Exception {

        assertThrows(NotFoundDataException.class,
                () -> {
                    is.update(ItemDto.builder().build(), 567L, 8274L);
                });
    }

    @Test
    void addItemWithUnrealRequest() throws Exception {
        ItemDto itemDto = ItemMapper.toDto(itemTestSearch);
        itemDto.setRequestId(999999L);

        assertThrows(NotFoundDataException.class,
                () -> {
                    is.add(itemDto, itemTestSearch.getId());
                });
    }

}
