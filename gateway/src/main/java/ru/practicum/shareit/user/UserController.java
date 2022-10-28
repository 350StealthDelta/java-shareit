package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.util.OnCreate;
import ru.practicum.shareit.util.OnUpdate;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserClient client;

    /**
     * Возвращает пользователя по id.
     *
     * @param id - id пользователя.
     * @return - пользователь.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Object> getUser(@PathVariable Long id) {
        log.info("=== Call 'getUser' with id {}",
                id);
        return client.getUser(id);
    }

    /**
     * Возвращает список пользователей.
     *
     * @return - список всех пользователей.
     */
    @GetMapping
    public ResponseEntity<Object> getAllUsers() {
        log.info("=== Call 'getAllUsers'.");
        return client.getAllUsers();
    }

    /**
     * Добавляет пользователя.
     *
     * @param userDto - данных пользователя в формате UserDto.
     * @return - добавленный пользователь в формате UserDto.
     */
    @PostMapping
    public ResponseEntity<Object> addUser(@RequestBody @Validated({OnCreate.class}) UserDto userDto) {
        log.info("=== Call 'addUser' with userDto {}",
                userDto);
        return client.addUser(userDto);
    }

    /**
     * Обновить данные пользователя.
     *
     * @param userDto - данные для обновления в формате UserDto.
     * @param userId  - id пользователя для обновления.
     * @return - пользователь с обновленными данными.
     */
    @PatchMapping("/{userId}")
    public ResponseEntity<Object> updateUser(@RequestBody @Validated({OnUpdate.class}) UserDto userDto,
                                             @PathVariable Long userId) {
        log.info("=== Call 'updateUser' with userDto {}, userId {}",
                userDto,
                userId);
        return client.updateUser(userId, userDto);
    }

    /**
     * Удаляет пользователя по id.
     *
     * @param id - id пользователя для удаления.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteUser(@PathVariable Long id) {
        log.info("=== Call 'deleteUser' with id {}",
                id);
        return client.deleteUser(id);
    }
}
