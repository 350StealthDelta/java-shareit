package ru.practicum.shareit.user.dao;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class UserRepositoryTest {
    @Autowired
    UserRepository repository;

    User user1;
    User user2;
    User user3;

    @BeforeEach
    void setUp() {
        user1 = repository.save(new User(1L, "User1", "user1@mail.com"));
        user2 = repository.save(new User(2L, "User2", "user2@mail.com"));
        user3 = repository.save(new User(3L, "User3", "user3@mail.com"));
    }

    @AfterEach
    void tearDown() {
        repository.deleteAll();
    }

    @Test
    void findByIdTest() {
        Optional<User> result = repository.findById(user1.getId());

        assertEquals(Optional.of(user1), result);

        result = repository.findById(-1L);

        assertEquals(Optional.empty(), result);
    }

    @Test
    void existsByIdTest() {
        boolean result = repository.existsById(user1.getId());

        assertTrue(result);

        result = repository.existsById(-1L);

        assertFalse(result);
    }

    @Test
    void findAllTest() {
        List<User> result = repository.findAll();

        assertEquals(3, result.size());
        assertEquals(user1.getId(), result.get(0).getId());
        assertEquals(user2.getName(), result.get(1).getName());
        assertEquals(user3.getEmail(), result.get(2).getEmail());
    }

    @Test
    void saveTest() {
        User testUser = new User(4L, "User4", "user4@mail.com");

        User result = repository.save(new User(4L, "User4", "user4@mail.com"));

        assertEquals(testUser, result);
    }

    @Test
    void deleteByIdTest() {
        repository.deleteById(user1.getId());

        List<User> result = repository.findAll();

        assertEquals(2, result.size());
        assertEquals(user2.getName(), result.get(0).getName());
        assertEquals(user3.getEmail(), result.get(1).getEmail());

        repository.deleteAll();

        result = repository.findAll();

        assertEquals(0, result.size());

    }
}