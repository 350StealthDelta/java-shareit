package ru.practicum.shareit.item.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.io.IOException;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class CommentDtoMapperTest {

    @Autowired
    private JacksonTester<CommentDto> json;

    @Test
    void commentDtoTest() throws IOException {
        CommentDto commentDto = CommentDto.builder()
                .id(1L)
                .text("text")
                .itemId(1L)
                .authorName("Name")
                .created(LocalDateTime.of(2022, 10, 16, 17, 20))
                .build();

        JsonContent<CommentDto> result = json.write(commentDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.text")
                .isEqualTo(commentDto.getText());
        assertThat(result).extractingJsonPathNumberValue("$.itemId").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.authorName")
                .isEqualTo(commentDto.getAuthorName());
    }
}