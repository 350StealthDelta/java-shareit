package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoForOut;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.util.CustomPageRequest;

import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemRequestController {

    private final ItemRequestService service;

    @PostMapping
    public ItemRequestDto addNewRequest(@RequestBody @Validated ItemRequestDto requestDto,
                                        @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("=== Call 'addNewRequest' with requestDto {} and userId {}.",
                requestDto, userId);
        return service.addRequest(requestDto, userId);
    }

    @GetMapping
    public List<ItemRequestDtoForOut> getAllOwnRequests(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("=== Call 'getAllOwnRequests' with userId {}.", userId);
        return service.getAllOwnRequests(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestDtoForOut> getAllPaging(@PositiveOrZero
                                                   @RequestParam(value = "from", defaultValue = "0") int from,
                                                   @PositiveOrZero
                                                   @RequestParam(value = "size", defaultValue = "10") int size,
                                                   @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("=== Call 'getAllPaging' with from {}, size {}, userId {}.",
                from, size, userId);
        final PageRequest pageRequest = CustomPageRequest.of(from, size);
        return service.getAllPaging(pageRequest, userId);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDtoForOut getById(@PathVariable(value = "requestId") Long requestId,
                                        @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("=== Call 'getById' with requestId {}, userId {}.",
                requestId, userId);
        return service.getById(requestId, userId);
    }
}
