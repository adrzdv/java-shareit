package ru.practicum.shareit;

import jakarta.transaction.Transactional;
import net.bytebuddy.utility.RandomString;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import ru.practicum.shareit.exceptions.NotFoundDataException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Transactional
@SpringBootTest
@Rollback(value = false)
public class UserServiceTest {

    @Autowired
    private UserService us;

    private long id;

    @BeforeEach
    void setUp() {

        String name = RandomString.make(RandomString.DEFAULT_LENGTH);
        String email = RandomString.make(RandomString.DEFAULT_LENGTH) + "@mail.com";
        User user = User.builder()
                .name(name)
                .email(email)
                .build();
        id = us.add(user).getId();
    }

    @Test
    void updateUser() throws Exception {
        String newEmail = RandomString.make(RandomString.DEFAULT_LENGTH) + "@mail.com";
        String newName = RandomString.make(RandomString.DEFAULT_LENGTH);
        User newUser = User.builder()
                .name(newName)
                .email(newEmail)
                .build();
        us.update(newUser, id);
        User userDb = us.get(id);

        assertThat(userDb.getName(), equalTo(newUser.getName()));
        assertThat(userDb.getEmail(), equalTo(newUser.getEmail()));

    }

    @Test
    void getUnrealUser() {

        assertThrows(NotFoundDataException.class,
                () -> {
                    us.get(999999999L);
                });
    }

    @Test
    void getAllExistingUsers() {

        List<UserDto> list = us.getAll();
        assertFalse(list.isEmpty());
    }

    @Test
    void deleteUser() {

        us.delete(id);
        assertThrows(NotFoundDataException.class,
                () -> {
                    us.get(id);
                });

    }

    @Test
    void updateEmail() throws Exception {
        String newEmail = RandomString.make(RandomString.DEFAULT_LENGTH) + "@example.com";
        User newUser = User.builder()
                .email(newEmail)
                .build();
        User oldUser = us.get(id);

        us.update(newUser, id);

        assertThat(us.get(id).getEmail(), equalTo(oldUser.getEmail()));
        assertThat(us.get(id).getName(), equalTo(oldUser.getName()));
    }

    @Test
    void updateName() throws Exception {

        String name = RandomString.make(RandomString.DEFAULT_LENGTH);
        User newUser = User.builder()
                .name(name)
                .build();
        User oldUser = us.get(id);

        us.update(newUser, id);

        assertThat(us.get(id).getEmail(), equalTo(oldUser.getEmail()));
        assertThat(us.get(id).getName(), equalTo(oldUser.getName()));

    }

}
