package ru.practicum.shareit.comment;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.exceptions.BookingException;
import ru.practicum.shareit.exceptions.NotFoundDataException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Component
@AllArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final UserService userService;
    private final BookingService bookingService;
    private final ItemRepository itemRepository;

    @Override
    public CommentDto postComment(long idItem, long idUser, String text) throws BookingException, NotFoundDataException {
        List<BookingDtoResponse> bookingDtoResponseList = bookingService.getByBooker(idUser, "ALL");

        if (bookingDtoResponseList
                .stream()
                .filter(bookingDtoResponse -> bookingDtoResponse.getItem().getId() == idItem)
                .filter(bookingDtoResponse -> bookingDtoResponse.getEnd().isBefore(LocalDateTime.now()))
                .findFirst()
                .isEmpty()) {
            throw new BookingException("Item was not booked by the user");
        }

        User user = userService.get(idUser);

        Optional<Item> itemOptional = itemRepository.findById(idItem);
        Item item;

        if (itemOptional.isEmpty()) {
            throw new NotFoundDataException("Item not found");
        } else {
            item = itemOptional.get();
        }

        Comment comment = Comment.builder()
                .text(text)
                .item(item)
                .author(user)
                .created(LocalDateTime.now())
                .build();

        Comment newComment = commentRepository.save(comment);

        return CommentMapper.toDto(newComment);
    }

}
