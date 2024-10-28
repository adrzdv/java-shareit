package ru.practicum.shareit.user;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exceptions.NotFoundDataException;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;
import java.util.Optional;

@Component
@AllArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public User add(User user) {

        return userRepository.save(user);
    }

    @Override
    public User update(User user, long id) {
        user.setId(id);
        User userDb = userRepository.findById(id).get();

        if (user.getEmail() == null) {
            user.setEmail(userDb.getEmail());
        } else if (user.getName() == null) {
            user.setName(userDb.getName());
        }

        return userRepository.save(user);
    }

    @Override
    public User get(long id) throws NotFoundDataException {

        Optional<User> userOptional = userRepository.findById(id);
        if (userOptional.isEmpty()) {
            throw new NotFoundDataException("User not found");
        }
        return userOptional.get();
    }

    @Override
    public List<UserDto> getAll() {

        List<User> userList = userRepository.findAll();
        List<UserDto> userDtoList = userList.stream()
                .map(UserMapper::toDto)
                .toList();
        return userDtoList;
    }

    @Override
    public void delete(long id) {
        userRepository.deleteById(id);
    }

}
