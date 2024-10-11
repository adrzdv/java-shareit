package ru.practicum.shareit.user;

import ru.practicum.shareit.exceptions.NotFoundDataException;
import ru.practicum.shareit.exceptions.NotUniqueEmail;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserStorage {

    /**
     * Add a new user
     *
     * @param user User object
     * @return User object from storage
     * @throws NotUniqueEmail
     * @throws NotFoundDataException
     */
    User add(User user) throws NotUniqueEmail, NotFoundDataException;

    /**
     * Update an existing User
     *
     * @param user User object for update
     * @param id   identification number
     * @return
     * @throws NotFoundDataException
     * @throws NotUniqueEmail
     */
    User update(User user, long id) throws NotFoundDataException, NotUniqueEmail;

    /**
     * Get an existing user by ID
     *
     * @param id identification number
     * @return User object
     * @throws NotFoundDataException
     */
    User get(long id) throws NotFoundDataException;

    /**
     * Get all existing users
     *
     * @return List of UserDTO objects
     */
    List<UserDto> getAll();

    /**
     * Delete an existing user
     *
     * @param id identification number
     * @throws NotFoundDataException
     */
    void delete(long id) throws NotFoundDataException;
}
