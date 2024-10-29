package ru.practicum.shareit.comment;

import ru.practicum.shareit.comment.dto.CommentDto;

public class CommentMapper {

    public static CommentDto toDto(Comment comment) {
        return CommentDto.builder()
                .id(comment.getId())
                .text(comment.getText())
                .authorName(comment.getAuthor().getName())
                .itemName(comment.getItem().getName())
                .created(comment.getCreated())
                .build();
    }
}
