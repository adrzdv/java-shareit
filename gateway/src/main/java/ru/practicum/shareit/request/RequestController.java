package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.RequestDto;

@Controller
@RequestMapping(value = "/requests")
@Slf4j
@RequiredArgsConstructor
public class RequestController {
    private final RequestClient requestClient;

    @PostMapping
    ResponseEntity<Object> add(@RequestBody RequestDto request,
                               @RequestHeader("X-Sharer-User-Id") long idUser) {

        return requestClient.add(idUser, request);
    }

    @GetMapping(value = "/all")
    ResponseEntity<Object> getAll(@RequestHeader("X-Sharer-User-Id") long idUser) {

        return requestClient.getAll(idUser);
    }

    @GetMapping
    ResponseEntity<Object> getRequesterList(@RequestHeader("X-Sharer-User-Id") long idUser) {

        return requestClient.getRequestorList(idUser);

    }

    @GetMapping(value = "/{id}")
    ResponseEntity<Object> getObject(@PathVariable long id) {
        return requestClient.getObject(id);
    }

}
