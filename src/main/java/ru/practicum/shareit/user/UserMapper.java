package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserDto;

public class UserMapper {

    /**
     * Method to transfer User to UserDTO object
     *
     * @param user user object for transfer
     * @return UserDTO object
     */
    public static UserDto toDto(User user) {
        return UserDto.builder()
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }
}
