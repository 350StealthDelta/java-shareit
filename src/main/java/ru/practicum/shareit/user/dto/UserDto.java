package ru.practicum.shareit.user.dto;

import lombok.*;
import ru.practicum.shareit.util.OnCreate;
import ru.practicum.shareit.util.OnUpdate;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
@ToString
public class UserDto {

    private Long id;
    @NotBlank(message = "Имя не должно быть пустым.", groups = {OnCreate.class})
    private String name;
    @NotBlank(message = "Email не должен быть пустым.", groups = {OnCreate.class})
    @Email(message = "Email должен быть валидным.", groups = {OnCreate.class, OnUpdate.class})
    private String email;
}
