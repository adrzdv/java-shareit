package ru.practicum.shareit.user.dto;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
public class UserDto {
    private String name;
    private String email;
}
