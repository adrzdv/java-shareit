package ru.practicum.shareit.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exceptions.NotFoundDataException;
import ru.practicum.shareit.exceptions.NotUniqueEmail;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.*;

@Data
@AllArgsConstructor
@Repository
@Slf4j
public class UserMemoryStorage implements UserStorage {
    private Map<Long, User> userMap;
    private Set<String> emailSet;

    @Override
    public User add(User user) throws NotUniqueEmail, NotFoundDataException {

        if (checkUniqueUser(user)) {
            if (checkUniqueEmail(user)) {
                long key = generateKey();
                key++;
                user.setId(key);
                userMap.put(user.getId(), user);
                emailSet.add(user.getEmail());
            }
        }

        return get(user.getId());

    }

    @Override
    public User update(User user, long id) throws NotFoundDataException, NotUniqueEmail {

        if (userMap.containsKey(id)) {
            user.setId(id);
            User userForUpdate = userMap.get(id);

            if (user.getEmail() == null && user.getName() != null) {
                userForUpdate.setName(user.getName());
                userMap.put(id, userForUpdate);
            } else if (user.getName() == null && user.getEmail() != null) {
                if (checkUniqueEmail(user)) {
                    if (!emailSet.contains(user.getEmail())) {
                        userForUpdate.setEmail(user.getEmail());
                        userMap.put(id, userForUpdate);
                    } else {
                        throw new NotUniqueEmail("Email is existing");
                    }
                }
            } else {
                if (checkUniqueEmail(user)) {
                    if (!emailSet.contains(user.getEmail())) {
                        userForUpdate.setEmail(user.getEmail());
                        userForUpdate.setName(user.getName());
                        userMap.put(id, userForUpdate);
                    } else {
                        throw new NotUniqueEmail("Email is existing");
                    }
                }
            }

        } else {
            throw new NotFoundDataException("User not found");
        }

        return get(id);
    }

    @Override
    public User get(long id) throws NotFoundDataException {

        if (!userMap.containsKey(id)) {
            throw new NotFoundDataException("User not found");
        }
        return userMap.get(id);
    }

    @Override
    public List<UserDto> getAll() {
        List<UserDto> result = new ArrayList<>();
        for (User user : userMap.values()) {
            result.add(UserMapper.toDto(user));
        }

        return result;
    }

    @Override
    public void delete(long id) throws NotFoundDataException {
        if (userMap.containsKey(id)) {
            userMap.remove(id);
        } else {
            throw new NotFoundDataException("User not found");
        }
    }

    /**
     * Methode for checking email uniqueness
     *
     * @param user User object for add/update
     * @return boolean
     * @throws NotUniqueEmail
     */
    private boolean checkUniqueEmail(User user) throws NotUniqueEmail {
        Optional<User> userCheck = userMap.values().stream()
                .filter(usr -> usr.getEmail().equals(user.getEmail()))
                .findAny();
        if (userCheck.isPresent()) {
            throw new NotUniqueEmail("Current email is busy");
        }
        return true;
    }

    /**
     * Method for checking user uniqueness
     *
     * @param user User object for add/update
     * @return boolean
     */
    private boolean checkUniqueUser(User user) {
        Optional<User> userCheck = userMap.values().stream()
                .filter(usr -> usr.equals(user))
                .findFirst();
        return userCheck.isEmpty();
    }

    /**
     * Method for generate key
     *
     * @return long key
     */
    private long generateKey() {
        return userMap.keySet().stream()
                .max((key1, key2) -> Math.toIntExact(key2 - key1))
                .orElse(1L);
    }

}
