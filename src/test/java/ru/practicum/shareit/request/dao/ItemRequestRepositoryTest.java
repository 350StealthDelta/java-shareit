package ru.practicum.shareit.request.dao;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.Rollback;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ItemRequestRepositoryTest {

    @Autowired
    ItemRequestRepository requestRepository;

    @Autowired
    UserRepository userRepository;

    ItemRequest itemRequest1;
    ItemRequest itemRequest2;
    ItemRequest itemRequest3;
    ItemRequest itemRequest4;
    ItemRequest itemRequest5;

    User user1;
    User user2;
    User user3;

    LocalDateTime time1;
    LocalDateTime time2;
    LocalDateTime time3;
    LocalDateTime time4;
    LocalDateTime time5;

    @BeforeEach
    void setUp() {
        user1 = userRepository.save(new User(1L, "User1", "user1@mail.com"));
        user2 = userRepository.save(new User(2L, "User2", "user2@mail.com"));
        user3 = userRepository.save(new User(3L, "User3", "user3@mail.com"));

        time1 = LocalDateTime.now().minusHours(1);
        time2 = LocalDateTime.now().minusHours(2);
        time3 = LocalDateTime.now().minusHours(3);
        time4 = LocalDateTime.now().minusHours(4);
        time5 = LocalDateTime.now().minusHours(5);

        itemRequest1 = requestRepository.save(new ItemRequest(1L, "desc1", user1, time1));
        itemRequest2 = requestRepository.save(new ItemRequest(2L, "desc2", user2, time2));
        itemRequest3 = requestRepository.save(new ItemRequest(3L, "desc3", user3, time3));
        itemRequest4 = requestRepository.save(new ItemRequest(4L, "desc4", user2, time4));
        itemRequest5 = requestRepository.save(new ItemRequest(5L, "desc5", user1, time5));
    }

    @AfterEach
    void tearDown() {
        requestRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    @Rollback
    void findAllByOwner() {
        List<ItemRequest> result = requestRepository.findAllByOwner(user2.getId());

        assertNotNull(result);
        assertTrue(result.contains(itemRequest2));
        assertTrue(result.contains(itemRequest4));
        assertFalse(result.contains(itemRequest1));
        assertFalse(result.contains(itemRequest3));
        assertFalse(result.contains(itemRequest5));
    }

    @Test
    @Rollback
    void findAllByRequestorIdIsNot() {
        List<ItemRequest> result = requestRepository
                .findAllByRequestorIdIsNot(user2.getId(), PageRequest.ofSize(10));

        assertNotNull(result);

        assertTrue(result.contains(itemRequest1));
        assertTrue(result.contains(itemRequest3));
        assertTrue(result.contains(itemRequest5));
        assertFalse(result.contains(itemRequest2));
        assertFalse(result.contains(itemRequest4));
    }
}