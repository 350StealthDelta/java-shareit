package ru.practicum.shareit.item.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class CommentDto {
    private Long id;
    private String text;
    private Long itemId;
    private String authorName;
    private LocalDateTime created;
}
