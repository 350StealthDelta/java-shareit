package ru.practicum.shareit.user.dao;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.model.User;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
public class UserInMemoryRepository implements UserDao {

    Map<Long, User> users = new HashMap<>();

    private Long id = 1L;

    @Override
    public User get(Long userId) {
        return users.get(userId);
    }

    @Override
    public List<User> getAll() {
        return users.entrySet().stream()
                .map(Map.Entry::getValue)
                .collect(Collectors.toUnmodifiableList());
    }

    @Override
    public User create(User user) {
        user.setId(id++);
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User update(User userUpdate, Long userId) {
        User user = users.get(userId);

        if (userUpdate.getName() != null) {
            user.setName(userUpdate.getName());
        }
        if (userUpdate.getEmail() != null) {
            user.setEmail(userUpdate.getEmail());
        }
        return user;
    }

    @Override
    public void delete(Long userId) {
        users.remove(userId);
    }

    @Override
    public boolean isUserExist(Long userId) {
        return users.containsKey(userId);
    }

    @Override
    public boolean isEmailExist(String email) {
        return users.entrySet().stream()
                .map(entryValue -> entryValue.getValue().getEmail())
                .collect(Collectors.toSet())
                .contains(email);
    }
}
