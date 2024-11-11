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
import ru.practicum.shareit.exceptions.NotFoundDataException;
import ru.practicum.shareit.request.ItemRequestController;
import ru.practicum.shareit.request.ItemRequestMapper;
import ru.practicum.shareit.request.ItemRequestService;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.model.RequestDescriptionObject;
import ru.practicum.shareit.user.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@WebMvcTest(controllers = ItemRequestController.class)
public class ItemRequestControllerTest {
    @Autowired
    private MockMvc mvc;
    @MockBean
    private ItemRequestService irs;
    @MockBean
    private User user;

    private final ObjectMapper mapper = JsonMapper.builder()
            .findAndAddModules()
            .build();

    private ItemRequest itemRequest = ItemRequest.builder()
            .id(1L)
            .description(RandomString.make(RandomString.DEFAULT_LENGTH))
            .created(LocalDateTime.now().plusNanos(9999L))
            .requestor(user)
            .build();
    private RequestDescriptionObject textObject = new RequestDescriptionObject(RandomString.make(20));

    @Test
    void addNewRequest() throws Exception {
        when(irs.add(anyLong(), anyString())).thenReturn(ItemRequestMapper.toDto(itemRequest));

        mvc.perform(post("/requests")
                        .content(mapper.writeValueAsString(textObject.getDescription()))
                        .header("X-Sharer-User-Id", "1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.description", is(itemRequest.getDescription())))
                .andExpect(jsonPath("$.created", is(itemRequest.getCreated().toString())));
    }

    @Test
    void returnNotFoundException() throws Exception {
        when(irs.finById(anyLong())).thenThrow(NotFoundDataException.class);

        mvc.perform(get("/requests/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getAllRequests() throws Exception {
        when(irs.findAll(anyLong()))
                .thenReturn(List.of(ItemRequestMapper.toDto(itemRequest)));

        mvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", "1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*", hasSize(1)));

    }

    @Test
    void getRequestsByRequestor() throws Exception {
        when(irs.findByRequester(anyLong()))
                .thenReturn(List.of(ItemRequestMapper.toDto(itemRequest)));

        mvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", "1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*", hasSize(1)));
    }

    @Test
    void getCurrentRequest() throws Exception {
        when(irs.finById(anyLong()))
                .thenReturn(ItemRequestMapper.toDto(itemRequest));

        mvc.perform(get("/requests/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description", is(itemRequest.getDescription())))
                .andExpect(jsonPath("$.created", is(itemRequest.getCreated().toString())));
    }

}
