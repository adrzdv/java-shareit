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

import java.util.*;


import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@Transactional
@SpringBootTest(properties = {"jdbc.url=jdbc:postgresql://localhost:5432/test"},
        webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@Rollback(value = false)
class AllAppTest {
    private final BookingService bs;
    private final ItemService is;
    private final UserService us;
    private final EntityManager em;
    private final ItemRequestService irs;

    private MockMvc mvc;


    @Autowired
    AllAppTest(EntityManager em, BookingService bs, ItemService is, UserService us, ItemRequestService irs) {
        this.bs = bs;
        this.is = is;
        this.us = us;
        this.em = em;
        this.irs = irs;
    }

    @BeforeEach
    void setup(WebApplicationContext wac) {
        mvc = MockMvcBuilders
                .webAppContextSetup(wac)
                .build();
    }

    @Test
    void saveNewItemByRequest() throws NotFoundDataException {

        int randomLength = 20;
        User userRequestor = User.builder()
                .name(RandomString.make(RandomString.DEFAULT_LENGTH))
                .email(RandomString.make(RandomString.DEFAULT_LENGTH) + "@example.com")
                .build();
        User userItemOwner = User.builder()
                .name(RandomString.make(RandomString.DEFAULT_LENGTH))
                .email(RandomString.make(RandomString.DEFAULT_LENGTH) + "@example.com")
                .build();

        us.add(userRequestor);
        us.add(userItemOwner);
        long userRequestorId = userRequestor.getId();
        long userOwnerId = userItemOwner.getId();

        ItemRequestDto itemRequestDto = irs.add(userRequestorId, RandomString.make(randomLength));
        long itemRequestId = itemRequestDto.getId();

        Item itemTest = Item.builder()
                .name(RandomString.make(RandomString.DEFAULT_LENGTH))
                .description(RandomString.make(RandomString.DEFAULT_LENGTH))
                .owner(us.get(userOwnerId))
                .available(true)
                .request(ItemRequestMapper.fromDto(itemRequestDto, userRequestor))
                .build();

        Item itemFromDb = is.add(ItemMapper.toDto(itemTest), userOwnerId);
        long idItem = itemFromDb.getId();

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

}