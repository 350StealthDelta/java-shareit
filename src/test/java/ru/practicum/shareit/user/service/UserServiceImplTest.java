package ru.practicum.shareit.user.service;

import com.google.gson.Gson;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserDtoMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.util.TimeAdapterGsonBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


class UserServiceImplTest {
    UserService service;
    UserRepository repository;
    static UserDtoMapper mapper;
    static Gson gson;
    User user1;
    User user2;
    User user3;

    @BeforeAll
    static void initialize() {
        mapper = new UserDtoMapper();
        gson = TimeAdapterGsonBuilder.getGson();
    }

    @BeforeEach
    void setUp() {
        user1 = User.builder()
                .id(1L)
                .name("User1")
                .email("user1@mail.com")
                .build();

        user2 = User.builder()
                .id(2L)
                .name("User2")
                .email("user2@mail.com")
                .build();

        user3 = User.builder()
                .id(3L)
                .name("User3")
                .email("user3@mail.com")
                .build();

        repository = mock(UserRepository.class);
        service = new UserServiceImpl(repository, mapper);
    }

    @AfterEach
    void tearDown() {
        repository = null;
        service = null;
    }

    @Test
    void getUser() {
        when(repository.findById(user1.getId()))
                .thenReturn(Optional.ofNullable(user1));
        when(repository.findById(-1L))
                .thenReturn(Optional.empty());

        UserDto userDto = service.getUser(user1.getId());

        assertNotNull(userDto);
        assertEquals(user1.getId(), userDto.getId());
        assertEquals(user1.getName(), userDto.getName());
        assertEquals(user1.getEmail(), userDto.getEmail());

        assertThrows(UserNotFoundException.class, () -> service.getUser(-1L));

        // Для 100% покрытия User.class
        assertEquals(user1.hashCode(), mapper.dtoToUser(userDto).hashCode());
        assertTrue(user1.equals(user1));
        assertFalse(user1.equals(null));
        assertTrue(user1.equals(mapper.dtoToUser(userDto)));
        assertFalse(user1.equals(user2));
        user1.setId(null);
        assertFalse(user1.equals(user2));


        verify(repository, times(2))
                .findById(anyLong());
    }

    @Test
    void getAllUsers() {
        List<User> users = new ArrayList<>();
        users.add(user1);
        users.add(user2);
        users.add(user3);

        List<UserDto> userDtos = users.stream()
                .map(mapper::userToUserDto)
                .collect(Collectors.toList());

        when(repository.findAll())
                .thenReturn(users);

        List<UserDto> result = service.getAllUsers();

        assertNotNull(result);
        assertEquals(userDtos.size(), result.size());
        assertEquals(userDtos.get(0).getId(), result.get(0).getId());
        assertEquals(userDtos.get(1).getName(), result.get(1).getName());
        assertEquals(userDtos.get(2).getEmail(), result.get(2).getEmail());

        verify(repository, times(1))
                .findAll();
    }

    @Test
    void addUser() {
        when(repository.save(user1))
                .thenReturn(user1);

        UserDto userDto = service.addUser(mapper.userToUserDto(user1));

        assertNotNull(userDto);
        assertEquals(user1.getId(), userDto.getId());
        assertEquals(user1.getName(), userDto.getName());
        assertEquals(user1.getEmail(), userDto.getEmail());

        verify(repository, times(1))
                .save(any(User.class));
    }

    @Test
    void editUser() {
        User user2mod = User.builder()
                .id(user1.getId())
                .name(user2.getName())
                .email(user2.getEmail())
                .build();

        UserDto nullUser = UserDto.builder()
                .id(null)
                .name(null)
                .email(null)
                .build();

        when(repository.findById(user1.getId()))
                .thenReturn(Optional.ofNullable(user1));
        when(repository.save(any(User.class)))
                .thenReturn(user2mod);

        UserDto userDto = service.editUser(mapper.userToUserDto(user2), user1.getId());

        assertNotNull(userDto);
        assertEquals(user2mod.getId(), userDto.getId());
        assertEquals(user2mod.getName(), userDto.getName());
        assertEquals(user2mod.getEmail(), userDto.getEmail());

        userDto = service.editUser(nullUser, user2mod.getId());

        assertNotNull(userDto);
        assertEquals(user2mod.getId(), userDto.getId());
        assertEquals(user2mod.getName(), userDto.getName());
        assertEquals(user2mod.getEmail(), userDto.getEmail());

        verify(repository, times(2))
                .save(any(User.class));
    }

    @Test
    void deleteUser() {
        when(repository.existsById(user1.getId()))
                .thenReturn(true);
        when(repository.existsById(-1L))
                .thenReturn(false);

        service.deleteUser(user1.getId());

        assertThrows(UserNotFoundException.class, () -> service.deleteUser(-1L));

        verify(repository, times(1))
                .deleteById(anyLong());
        verify(repository, times(2))
                .existsById(anyLong());
    }
}