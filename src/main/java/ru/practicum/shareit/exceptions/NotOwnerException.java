package ru.practicum.shareit.exceptions;

public class NotOwnerException extends Exception {
    public NotOwnerException(String message) {
        super(message);
    }
}
