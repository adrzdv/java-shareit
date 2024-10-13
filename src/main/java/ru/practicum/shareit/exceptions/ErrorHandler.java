package ru.practicum.shareit.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorCustomResponse notUniqueEmail(final NotUniqueEmail e) {
        return new ErrorCustomResponse("Error", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorCustomResponse duplicateData(final DuplicateException e) {
        return new ErrorCustomResponse("Error", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorCustomResponse notFoundData(final NotFoundDataException e) {
        return new ErrorCustomResponse("Error", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorCustomResponse notOwner(final NotOwnerException e) {
        return new ErrorCustomResponse("Error", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorCustomResponse validationException(final ValidationException e) {
        return new ErrorCustomResponse("Error", e.getMessage());
    }

}
