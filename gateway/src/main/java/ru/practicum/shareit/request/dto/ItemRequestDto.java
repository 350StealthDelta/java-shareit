package ru.practicum.shareit.request.dto;

import lombok.*;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class ItemRequestDto {
    private Long id;
    @NotBlank
    private String description;
    private Long userId;
    private LocalDateTime created;
}
