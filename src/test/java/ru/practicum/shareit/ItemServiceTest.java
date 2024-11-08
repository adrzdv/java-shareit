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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.booking.dto.BookingDto;
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
import static org.junit.jupiter.api.Assertions.assertThrows;

@Transactional
@SpringBootTest(properties = {"jdbc.url=jdbc:postgresql://localhost:5432/test"},
        webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@Rollback(value = false)
public class ItemServiceTest {

    private final ItemService is;
    private final UserService us;
    private final EntityManager em;
    private final BookingService bs;

    private MockMvc mvc;

    @Autowired
    ItemServiceTest(EntityManager em, ItemService is, UserService us, BookingService bs) {
        this.is = is;
        this.us = us;
        this.em = em;
        this.bs = bs;
    }

    @BeforeEach
    void setup(WebApplicationContext wac) {
        mvc = MockMvcBuilders
                .webAppContextSetup(wac)
                .build();
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
                .description(RandomString.make(RandomString.DEFAULT_LENGTH))
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
        assertThat(item.getId(), equalTo(item.getId()));
        assertThat(item.getName(), equalTo(itemForUpdate.getName()));
        assertThat(item.getDescription(), equalTo(itemTest.getDescription()));
        assertThat(item.getOwner(), equalToObject(userItemOwner));

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
                .description(RandomString.make(RandomString.DEFAULT_LENGTH))
                .owner(userOwner)
                .available(true)
                .request(null)
                .build();

        Item item = is.add(ItemMapper.toDto(itemTest), itemTest.getOwner().getId());

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
}
