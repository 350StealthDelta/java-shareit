package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.UserEmailAlreadyExistException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.user.dao.UserDao;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserDtoMapper;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserDao dao;

    @Override
    public UserDto getUser(Long userId) {
        isUserExist(userId);
        return UserDtoMapper.userToUserDto(dao.get(userId));
    }

    @Override
    public List<UserDto> getAllUsers() {
        return dao.getAll().stream()
                .map(UserDtoMapper::userToUserDto)
                .collect(Collectors.toUnmodifiableList());
    }

    @Override
    public UserDto addUser(UserDto userDto) {
        isEmailExist(userDto.getEmail());
        return UserDtoMapper.userToUserDto(
                dao.create(
                        UserDtoMapper.dtoToUser(userDto)));
    }

    @Override
    public UserDto editUser(UserDto userDto, Long userId) {
        isUserExist(userId);
        isEmailExist(userDto.getEmail());
        User user = UserDtoMapper.dtoToUser(userDto);
        return UserDtoMapper.userToUserDto(
                dao.update(user, userId));
    }

    @Override
    public void deleteUser(Long userId) {
        isUserExist(userId);
        dao.delete(userId);
    }

    private void isUserExist(Long userId) {
        if (!dao.isUserExist(userId)) {
            throw new UserNotFoundException(
                    String.format("Пользователь с id = %s не найден.", userId));
        }
    }

    private void isEmailExist(String email) {
        if (dao.isEmailExist(email)) {
            throw new UserEmailAlreadyExistException(
                    String.format("Пользователь с email = %s уже существует.", email));
        }
    }
}
