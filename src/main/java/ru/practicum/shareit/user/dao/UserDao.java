package ru.practicum.shareit.user.dao;

import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserDao {

    User get(Long userId);

    List<User> getAll();

    User create(User user);

    User update(User user, Long userId);

    void delete(Long userId);

    boolean isUserExist(Long userId);

    boolean isEmailExist(String email);
}
