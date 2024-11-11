package ru.practicum.shareit.comment;

import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.exceptions.NotFoundDataException;

public interface CommentService {

    CommentDto postComment(long idItem, long idUser, String text) throws NotFoundDataException;

}
