package ru.practicum.shareit.user.booking;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import ru.practicum.shareit.booking.dto.BookingRequestDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class BookingRequestDtoJsonTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldSerializeCorrectly() throws JsonProcessingException {
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);

        BookingRequestDto dto = BookingRequestDto.builder()
                .itemId(1L)
                .start(start)
                .end(end)
                .build();

        String json = objectMapper.writeValueAsString(dto);

        assertThat(json).contains("\"itemId\":1");
        assertThat(json).contains("\"start\":\"" + start.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        assertThat(json).contains("\"end\":\"" + end.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
    }

    @Test
    void shouldDeserializeCorrectly() throws JsonProcessingException {
        String json = "{\"itemId\":1,\"start\":\"2025-12-30T10:00:00\",\"end\":\"2025-12-31T10:00:00\"}";

        BookingRequestDto dto = objectMapper.readValue(json, BookingRequestDto.class);

        assertThat(dto.getItemId()).isEqualTo(1L);
        assertThat(dto.getStart()).isEqualTo(LocalDateTime.of(2025, 12, 30, 10, 0, 0));
        assertThat(dto.getEnd()).isEqualTo(LocalDateTime.of(2025, 12, 31, 10, 0, 0));
    }

    @Test
    void shouldHandleNullFields() throws JsonProcessingException {
        String json = "{\"itemId\":null,\"start\":null,\"end\":null}";

        BookingRequestDto dto = objectMapper.readValue(json, BookingRequestDto.class);

        assertThat(dto.getItemId()).isNull();
        assertThat(dto.getStart()).isNull();
        assertThat(dto.getEnd()).isNull();
    }
}