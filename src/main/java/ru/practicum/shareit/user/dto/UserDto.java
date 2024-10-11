package ru.practicum.shareit.user.dto;

import lombok.*;
import ru.practicum.shareit.user.User;

@Data
@Builder
@AllArgsConstructor
public class UserDto {
    private String name;
    private String email;

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
