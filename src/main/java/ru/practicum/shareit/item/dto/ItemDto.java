package ru.practicum.shareit.item.dto;

import lombok.*;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.util.OnCreate;
import ru.practicum.shareit.util.OnUpdate;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
@ToString
public class ItemDto {
    private Long id;
    @NotBlank(message = "Название не может быть пустым.", groups = {OnCreate.class})
    private String name;
    @NotBlank(message = "Описание не должно быть пустым", groups = {OnCreate.class})
    private String description;
    @NotNull(message = "Available должна быть указана", groups = {OnCreate.class, OnUpdate.class})
    private Boolean available;
    private ItemRequest request;
}
