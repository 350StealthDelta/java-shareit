package ru.practicum.shareit.user;

import com.google.gson.Gson;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserDtoMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.util.TimeAdapterGsonBuilder;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc
class UserControllerTest {

    @MockBean
    UserService userService;
    @Autowired
    MockMvc mockMvc;
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
    }

    @Test
    void getUser() throws Exception {
        when(userService.getUser(user1.getId()))
                .thenReturn(mapper.userToUserDto(user1));

        mockMvc.perform(get("/users/1"))
                .andExpect(status().isOk())
                .andExpect(content().json(gson.toJson(mapper.userToUserDto(user1))));

        verify(userService, times(1))
                .getUser(anyLong());
    }

    @Test
    void getAllUsers() throws Exception {
        List<UserDto> users = new ArrayList<>();
        users.add(mapper.userToUserDto(user1));
        users.add(mapper.userToUserDto(user2));
        users.add(mapper.userToUserDto(user3));

        when(userService.getAllUsers())
                .thenReturn(users);

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(content().json(gson.toJson(users)));

        verify(userService, times(1))
                .getAllUsers();
    }

    @Test
    void addUser() throws Exception {
        when(userService.addUser(mapper.userToUserDto(user1)))
                .thenReturn(mapper.userToUserDto(user1));

        mockMvc.perform(post("/users")
                        .content(gson.toJson(mapper.userToUserDto(user1)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(gson.toJson(mapper.userToUserDto(user1))));

        verify(userService, times(1))
                .addUser(any(UserDto.class));
    }

    @Test
    void updateUser() throws Exception {
        when(userService.editUser(mapper.userToUserDto(user2), user2.getId()))
                .thenReturn(mapper.userToUserDto(user2));

        mockMvc.perform(patch("/users/2")
                        .content(gson.toJson(mapper.userToUserDto(user2)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(gson.toJson(mapper.userToUserDto(user2))));

        verify(userService, times(1))
                .editUser(any(UserDto.class), anyLong());
    }

    @Test
    void deleteUser() throws Exception {
        mockMvc.perform(delete("/users/3"))
                .andExpect(status().isOk());

        verify(userService, times(1))
                .deleteUser(anyLong());
    }
}