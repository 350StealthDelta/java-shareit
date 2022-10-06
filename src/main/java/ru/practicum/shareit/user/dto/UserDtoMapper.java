package ru.practicum.shareit.user.dto;

import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.model.User;

@Component
@NoArgsConstructor
public class UserDtoMapper {

    public UserDto userToUserDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }

    public User dtoToUser(UserDto dto) {
        return User.builder()
                .id(dto.getId())
                .name(dto.getName())
                .email(dto.getEmail())
                .build();
    }
}
