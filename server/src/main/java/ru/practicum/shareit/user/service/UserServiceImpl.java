package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserDtoMapper;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository dao;
    private final UserDtoMapper mapper;

    @Override
    public UserDto getUser(Long userId) {
        return mapper.userToUserDto(getUserById(userId));
    }

    @Override
    public List<UserDto> getAllUsers() {
        return dao.findAll().stream()
                .map(mapper::userToUserDto)
                .collect(Collectors.toUnmodifiableList());
    }

    @Override
    @Transactional
    public UserDto addUser(UserDto userDto) {
        return mapper.userToUserDto(
                dao.save(mapper.dtoToUser(userDto)));
    }

    @Override
    @Transactional
    public UserDto editUser(UserDto userDto, Long userId) {
        User userFromRepository = getUserById(userId);
        User user = mapper.dtoToUser(userDto);
        user.setId(userId);
        usersFieldsMapping(user, userFromRepository);
        return mapper.userToUserDto(
                dao.save(user));
    }

    @Override
    @Transactional
    public void deleteUser(Long userId) {
        isUserExist(userId);
        dao.deleteById(userId);
    }

    private void isUserExist(Long userId) {
        if (!dao.existsById(userId)) {
            throw new UserNotFoundException(String
                    .format("Пользователь с id = %s не найден.", userId));
        }
    }

    private User getUserById(Long userId) {
        return dao.findById(userId).orElseThrow(() -> new UserNotFoundException(String
                .format("Пользователь с id = %s не найден.", userId)));
    }

    private void usersFieldsMapping(User userFor, User userFrom) {
        // Переделать через рефлексию
/*        if (userFor.getId() == null) {
            userFor.setId(userFrom.getId());
        }*/
        if (userFor.getName() == null) {
            userFor.setName(userFrom.getName());
        }
        if (userFor.getEmail() == null) {
            userFor.setEmail(userFrom.getEmail());
        }
    }
}
