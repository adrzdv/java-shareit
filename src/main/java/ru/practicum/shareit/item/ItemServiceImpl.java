package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.comment.CommentMapper;
import ru.practicum.shareit.comment.CommentRepository;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.exceptions.NotFoundDataException;
import ru.practicum.shareit.exceptions.NotOwnerException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoResponse;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequestMapper;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.request.ItemRequestService;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;

import java.time.LocalDateTime;
import java.util.*;

@Component
@AllArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final UserService userService;
    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final ItemRequestService itemRequestService;
    private final ItemRequestRepository itemRequestRepository;

    @Override
    public Item add(ItemDto itemDto, long id) throws NotFoundDataException {

        Item item;
        if (itemDto.getRequestId() != 0) {
            ItemRequestDto itemRequestDto = itemRequestService.finById(itemDto.getRequestId());
            Optional<ItemRequest> itemRequestOptional = itemRequestRepository.findById(itemRequestDto.getId());
            if (itemRequestOptional.isEmpty()) {
                throw new NotFoundDataException("Request not found");
            }

            User requestor = userService.get(itemRequestOptional.get().getRequestor().getId());

            ItemRequest itemRequest = ItemRequestMapper.fromDto(itemRequestDto, requestor);
            item = ItemMapper.fromDtoWithRequest(itemDto, userService.get(id), itemRequest);
        } else {
            item = ItemMapper.fromDto(itemDto, userService.get(id));
        }
        return itemRepository.save(item);
    }

    @Override
    public Item update(ItemDto itemDto, long idUser, long idItem) throws NotOwnerException, NotFoundDataException {
        if (!checkOwner(idUser, idItem)) {
            throw new NotOwnerException("Not owner of item");
        }

        User user = userService.get(idUser);
        Item newItem = ItemMapper.fromDto(itemDto, user);
        Optional<Item> oldItemOptional = itemRepository.findById(idItem);
        if (oldItemOptional.isEmpty()) {
            throw new NotFoundDataException("Item not found");
        }
        Item oldItem = oldItemOptional.get();
        if (newItem.getName() == null) {
            newItem.setName(oldItem.getName());
        }
        if (newItem.getDescription() == null) {
            newItem.setDescription(oldItem.getDescription());
        }
        if (itemDto.getAvailable() == null) {
            newItem.setAvailable(oldItem.isAvailable());
        }

        newItem.setId(idItem);

        return itemRepository.save(newItem);
    }

    @Override
    public ItemDtoResponse get(long id) throws NotFoundDataException {

        Optional<Item> itemOptional = itemRepository.findById(id);
        if (itemOptional.isEmpty()) {
            throw new NotFoundDataException("Item not found");
        }

        ItemDtoResponse result = ItemMapper.toDtoWithBookingDates(itemOptional.get());
        List<BookingDtoResponse> bookingList = bookingRepository.findByItem_id(id).stream()
                .map(BookingMapper::toDtoResponse)
                .toList();
        Map<String, LocalDateTime> dateTimeMap = new HashMap<>();
        if (!bookingList.isEmpty()) {
            if (bookingList.size() == 1) {
                if (bookingList.getFirst().getStart().isAfter(LocalDateTime.now())) {
                    dateTimeMap.put("Past", null);
                    dateTimeMap.put("Next", bookingList.getFirst().getStart());
                } else if (bookingList.getFirst().getEnd().isBefore(LocalDateTime.now())) {
                    dateTimeMap.put("Past", bookingList.getFirst().getEnd());
                    dateTimeMap.put("Next", null);
                }
            } else {
                dateTimeMap = getLastAndEndDate(bookingList, itemOptional.get().getId());
            }
        } else {
            dateTimeMap.put("Past", null);
            dateTimeMap.put("Next", null);
        }

        List<CommentDto> commentList = commentRepository.findByItem_id(id).stream()
                .map(CommentMapper::toDto)
                .toList();

        result.setLastBooking(dateTimeMap.get("Past"));
        result.setNextBooking(dateTimeMap.get("Next"));
        result.setComments(commentList);
        result.setItemRequests(itemOptional.get().getRequest());

        return result;
    }

    @Override
    public void delete(long id) throws NotFoundDataException {
        itemRepository.deleteById(id);
    }

    @Override
    public List<ItemDtoResponse> getUserItems(long id) throws NotFoundDataException {

        if (userService.get(id) == null) {
            throw new NotFoundDataException("User not found");
        }

        List<BookingDtoResponse> bookingList = bookingRepository.getBookingsByOwnerId(id).stream()
                .map(BookingMapper::toDtoResponse)
                .sorted(Comparator.comparing(BookingDtoResponse::getStart))
                .toList();

        List<ItemDtoResponse> temp = itemRepository.findByOwner_id(id).stream()
                .map(ItemMapper::toDtoWithBookingDates)
                .toList();

        Map<Long, ItemDtoResponse> itemMap = new HashMap<>();

        for (ItemDtoResponse item : temp) {
            itemMap.put(item.getId(), item);
        }

        for (BookingDtoResponse booking : bookingList) {
            if (itemMap.containsKey(booking.getItem().getId())) {
                Map<String, LocalDateTime> dateTimeMap = getLastAndEndDate(bookingList, booking.getItem().getId());
                itemMap.get(booking.getItem().getId()).setNextBooking(dateTimeMap.get("Next"));
                itemMap.get(booking.getItem().getId()).setLastBooking(dateTimeMap.get("Past"));
            }
        }

        return new ArrayList<>(itemMap.values());
    }


    @Override
    public List<ItemDto> search(String searchString) {
        List<ItemDto> result = new ArrayList<>();
        if (searchString.equalsIgnoreCase("")) {
            return result;
        }

        return itemRepository.search(searchString).stream()
                .filter(Item::isAvailable)
                .map(ItemMapper::toDto)
                .toList();

    }

    /**
     * Method for check owner of updating item
     *
     * @param idUser identification number of user
     * @param idItem identification number of item
     * @return boolean true - if it's owner of item
     * @throws NotFoundDataException
     */
    private boolean checkOwner(long idUser, long idItem) throws NotFoundDataException {

        Optional<Item> itemOptional = itemRepository.findById(idItem);
        if (itemOptional.isEmpty()) {
            throw new NotFoundDataException("Item not found");
        }

        Item item = itemOptional.get();
        return item.getOwner().getId() == idUser;

    }

    /**
     * Method for search previous and next booking
     *
     * @param bookingList list of item's booking
     * @param idItems     item's id
     * @return Map of last and next LocalDateTime data of booking
     */
    private Map<String, LocalDateTime> getLastAndEndDate(List<BookingDtoResponse> bookingList, long idItems) {
        Map<String, LocalDateTime> result = new HashMap<>();

        Optional<BookingDtoResponse> bookingNext = bookingList.stream()
                .filter(bookingDtoResponse -> bookingDtoResponse.getItem().getId() == idItems)
                .filter(bookingDtoResponse -> bookingDtoResponse.getStart().isAfter(LocalDateTime.now()))
                .findFirst();

        if (bookingNext.isPresent()) {
            result.put("Next", bookingNext.get().getStart());
            Optional<BookingDtoResponse> bookingPast = bookingList.stream()
                    .filter(bookingDtoResponse -> bookingDtoResponse.getItem().getId() == idItems)
                    .filter(bookingDtoResponse -> bookingDtoResponse.getEnd().isBefore(LocalDateTime.now()))
                    .filter(bookingDtoResponse -> bookingDtoResponse.getId() != bookingNext.get().getId())
                    .max(Comparator.comparing(BookingDtoResponse::getEnd));
            bookingPast.ifPresent(bookingDtoResponse -> result.put("Past", bookingDtoResponse.getEnd()));
        } else {
            Optional<BookingDtoResponse> bookingPast = bookingList.stream()
                    .filter(bookingDtoResponse -> bookingDtoResponse.getItem().getId() == idItems)
                    .filter(bookingDtoResponse -> bookingDtoResponse.getEnd().isBefore(LocalDateTime.now()))
                    .max(Comparator.comparing(BookingDtoResponse::getEnd));
            bookingPast.ifPresent(bookingDtoResponse -> result.put("Past", bookingDtoResponse.getEnd()));
        }

        return result;
    }
}
