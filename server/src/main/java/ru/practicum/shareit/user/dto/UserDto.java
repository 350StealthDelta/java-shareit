package ru.practicum.shareit.user.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
@ToString
public class UserDto {

    private Long id;
    private String name;
    private String email;
}
