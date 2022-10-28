package ru.practicum.shareit.booking.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.io.IOException;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class BookingDtoMapperTest {
    @Autowired
    private JacksonTester<BookingDto> bookingDtoJacksonTester;
    @Autowired
    private JacksonTester<BookingDtoInput> bookingDtoInputJacksonTester;

    @Test
    void bookingDtoTest() throws IOException {
        BookingDto dto = BookingDto.builder()
                .id(1L)
                .start(LocalDateTime.of(2022, 10, 15, 17, 45))
                .end(LocalDateTime.of(2022, 11, 20, 15, 00))
                .status("WAITING")
                .build();

        dto.setItem(dto.createItem(1L, "name"));
        dto.setBooker(dto.createBooker(1L));

        JsonContent<BookingDto> result = bookingDtoJacksonTester.write(dto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.status").isEqualTo(dto.getStatus());
        assertThat(result).extractingJsonPathNumberValue("$.item.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.item.name").isEqualTo("name");
        assertThat(result).extractingJsonPathNumberValue("$.booker.id").isEqualTo(1);
    }

    @Test
    void bookingDtoInputTest() throws IOException {
        BookingDtoInput dtoInput = BookingDtoInput.builder()
                .itemId(1L)
                .start(LocalDateTime.of(2022, 10, 15, 17, 45))
                .end(LocalDateTime.of(2022, 11, 20, 15, 00))
                .build();

        JsonContent<BookingDtoInput> result = bookingDtoInputJacksonTester.write(dtoInput);

        assertThat(result).extractingJsonPathNumberValue("$.itemId").isEqualTo(1);
    }
}