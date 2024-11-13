package ru.practicum.shareit;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import net.bytebuddy.utility.RandomString;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.comment.CommentService;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.model.CommentDescriptionObject;
import ru.practicum.shareit.exceptions.NotOwnerException;
import ru.practicum.shareit.item.ItemController;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class)
public class ItemControllerTest {
    @Autowired
    private MockMvc mvc;
    @MockBean
    ItemService is;
    @MockBean
    CommentService cs;

    private final ObjectMapper mapper = JsonMapper.builder()
            .findAndAddModules()
            .build();

    private final Item item = Item.builder()
            .id(1L)
            .name(RandomString.make(RandomString.DEFAULT_LENGTH))
            .description(RandomString.make(RandomString.DEFAULT_LENGTH))
            .available(true)
            .owner(User.builder()
                    .id(1L)
                    .name(RandomString.make(RandomString.DEFAULT_LENGTH))
                    .email(RandomString.make(RandomString.DEFAULT_LENGTH) + "@example.com")
                    .build())
            .build();

    @Test
    void addNewItem() throws Exception {
        when(is.add(any(), anyLong())).thenReturn(item);

        mvc.perform(post("/items")
                        .content(mapper.writeValueAsString(item))
                        .header("X-Sharer-User-Id", "1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.name", is(item.getName())));
    }

    @Test
    void updateItem() throws Exception {
        when(is.update(ItemMapper.toDto(item), item.getOwner().getId(), item.getId()))
                .thenReturn(item);
        when(is.update(ItemMapper.toDto(item), 2L, item.getId()))
                .thenThrow(NotOwnerException.class);

        mvc.perform(patch("/items/1")
                        .content(mapper.writeValueAsString(item))
                        .header("X-Sharer-User-Id", "1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.name", is(item.getName())));

        mvc.perform(patch("/items/1")
                        .content(mapper.writeValueAsString(item))
                        .header("X-Sharer-User-Id", "2")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());

    }

    @Test
    void getItemById() throws Exception {
        when(is.get(1L)).thenReturn(ItemMapper.toDtoWithBookingDates(item));

        mvc.perform(get("/items/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.name", is(item.getName())));

    }

    //get, header
    @Test
    void getUserItems() throws Exception {
        when(is.getUserItems(1L)).thenReturn(List.of(ItemMapper.toDtoWithBookingDates(item)));

        mvc.perform(get("/items")
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*", hasSize(1)));

    }

    @Test
    void search() throws Exception {
        when(is.search(anyString())).thenReturn(List.of(ItemMapper.toDto(item)));

        mvc.perform(get("/items/search").param("text", "text")).andExpectAll(
                status().isOk(),
                jsonPath("$.*", hasSize(1))
        );

    }

    @Test
    void postComment() throws Exception {

        CommentDto commentDto = CommentDto.builder()
                .id(1L)
                .text(RandomString.make(RandomString.DEFAULT_LENGTH))
                .authorName(RandomString.make(RandomString.DEFAULT_LENGTH))
                .created(LocalDateTime.now())
                .build();
        CommentDescriptionObject comment = new CommentDescriptionObject(RandomString.make(RandomString.DEFAULT_LENGTH));
        when(cs.postComment(anyLong(), anyLong(), anyString()))
                .thenReturn(commentDto);

        mvc.perform(post("/items/1/comment")
                .header("X-Sharer-User-Id", "1")
                .content(mapper.writeValueAsString(comment))
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.text", is(commentDto.getText())))
                .andExpect(jsonPath("$.created", is(commentDto.getCreated().toString())));

    }

}
