package ru.practicum.shareit.request;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exceptions.NotFoundDataException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDtoShort;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
@AllArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {
    ItemRequestRepository itemRequestRepository;
    UserService userService;
    ItemRepository itemRepository;

    @Override
    public ItemRequestDto add(long idRequester, String description) throws NotFoundDataException {
        User requester = userService.get(idRequester);
        ItemRequest itemRequest = ItemRequest.builder()
                .requestor(requester)
                .created(LocalDateTime.now())
                .description(description)
                .build();
        ItemRequest itemRequestFromDb = itemRequestRepository.save(itemRequest);
        return ItemRequestMapper.toDto(itemRequestFromDb);
    }

    @Override
    public ItemRequestDto finById(long id) throws NotFoundDataException {
        Optional<ItemRequest> itemRequestOptional = itemRequestRepository.findById(id);
        if (itemRequestOptional.isEmpty()) {
            throw new NotFoundDataException("Request not found");
        }
        List<ItemDtoShort> item = itemRepository.findByRequest_Id(id).stream()
                .map(ItemMapper::toDtoShort)
                .toList();
        ItemRequestDto itemRequestDto = ItemRequestMapper.toDto(itemRequestOptional.get());
        itemRequestDto.setItems(item);
        return itemRequestDto;
    }

    @Override
    public List<ItemRequestDto> findAll(long idRequestor) {
        List<ItemRequest> list = itemRequestRepository.findAll(Sort.by(Sort.Direction.DESC, "created"));
        return list.stream()
                .filter(ItemRequest -> ItemRequest.getRequestor().getId() != idRequestor)
                .map(ItemRequestMapper::toDto)
                .toList();
    }

    @Override
    public List<ItemRequestDto> findByRequester(long idRequestor) {
        List<Item> itemList = itemRepository.findByRequestorId(idRequestor);

        List<ItemRequest> list = itemRequestRepository.findAll(Sort.by(Sort.Direction.DESC, "created"));
        return list.stream()
                .filter(ItemRequest -> ItemRequest.getRequestor().getId() == idRequestor)
                .map(ItemRequestMapper::toDto)
                .peek(itemRequestDto -> itemRequestDto.setItems(new ArrayList<>()))
                .peek(itemRequestDto -> {
                            for (Item item : itemList) {
                                if (item.getRequest().getId() == itemRequestDto.getId()) {
                                    itemRequestDto.getItems().add(ItemMapper.toDtoShort(item));
                                }
                            }
                        }
                )
                .toList();
    }
}
