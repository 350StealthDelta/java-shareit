package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {

    UserDto getUser(Long userId);

    List<UserDto> getAllUsers();

    UserDto addUser(UserDto userDto);

    UserDto editUser(UserDto userDto, Long userId);

    void deleteUser(Long userId);
}
