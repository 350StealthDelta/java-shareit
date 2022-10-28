package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import javax.validation.constraints.PositiveOrZero;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemRequestController {

    private final ItemRequestClient client;

    @PostMapping
    public ResponseEntity<Object> addNewRequest(@RequestBody @Validated ItemRequestDto requestDto,
                                                @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("=== Call 'addNewRequest' with requestDto {} and userId {}.",
                requestDto, userId);
        return client.addNewRequest(requestDto, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllOwnRequests(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("=== Call 'getAllOwnRequests' with userId {}.", userId);
        return client.getAllOwnRequests(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllPaging(@PositiveOrZero
                                               @RequestParam(value = "from", defaultValue = "0") int from,
                                               @PositiveOrZero
                                               @RequestParam(value = "size", defaultValue = "10") int size,
                                               @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("=== Call 'getAllPaging' with from {}, size {}, userId {}.",
                from, size, userId);
        return client.getAllPaging(from, size, userId);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getById(@PathVariable(value = "requestId") Long requestId,
                                          @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("=== Call 'getById' with requestId {}, userId {}.",
                requestId, userId);
        return client.getById(requestId, userId);
    }
}
