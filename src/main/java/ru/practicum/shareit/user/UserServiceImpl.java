package ru.practicum.shareit.user;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exceptions.NotFoundDataException;
import ru.practicum.shareit.exceptions.NotUniqueEmail;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

@Component
@AllArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserStorage userStorage;

    @Override
    public User add(User user) throws NotUniqueEmail, NotFoundDataException {
        return userStorage.add(user);
    }

    @Override
    public User update(User user, long id) throws NotFoundDataException, NotUniqueEmail {
        return userStorage.update(user, id);
    }

    @Override
    public User get(long id) throws NotFoundDataException {
        return userStorage.get(id);
    }

    @Override
    public List<UserDto> getAll() {
        return userStorage.getAll();
    }

    @Override
    public void delete(long id) throws NotFoundDataException {
        userStorage.delete(id);
    }

}
