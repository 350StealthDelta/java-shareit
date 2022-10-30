package ru.practicum.shareit.request.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class ItemRequestDto {
    private Long id;
    private String description;
    private Long userId;
    private LocalDateTime created;
}
