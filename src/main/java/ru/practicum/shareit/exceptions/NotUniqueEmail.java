package ru.practicum.shareit.exceptions;

public class NotUniqueEmail extends Exception {
    public NotUniqueEmail(String message) {
        super(message);
    }
}
