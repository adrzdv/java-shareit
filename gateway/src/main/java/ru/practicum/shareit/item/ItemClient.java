package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;

import org.springframework.stereotype.Component;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.item.dto.CommentDescription;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.Map;

@Component
public class ItemClient extends BaseClient {
    private static final String API_PREFIX = "/items";

    @Autowired
    public ItemClient(@Value("${shareit-server.uri}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                        .build()
        );
    }


    ResponseEntity<Object> addItem(long idUser, ItemDto itemDto) {
        return post("", idUser, itemDto);
    }

    ResponseEntity<Object> update(long idUser, ItemDto itemDto, long idItem) {

        return patch("/" + idItem, idUser, itemDto);
    }

    ResponseEntity<Object> get(long idItem) {

        return get("/" + idItem);
    }

    ResponseEntity<Object> getUserItems(long idUser) {

        return get("", idUser);
    }

    ResponseEntity<Object> delete(long id) {

        return delete("/" + id);
    }

    ResponseEntity<Object> search(String text) {
        Map<String, Object> parameters = Map.of(
                "text", text
        );
        return get("/search?text={text}", parameters);
    }

    ResponseEntity<Object> postComment(long idItem, long idUser, CommentDescription comment) {
        return post("/" + idItem + "/comment", idUser, comment);
    }

}
