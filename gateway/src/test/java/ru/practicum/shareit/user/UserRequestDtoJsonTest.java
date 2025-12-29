package ru.practicum.shareit.user;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import ru.practicum.shareit.user.dto.UserRequestDto;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class UserRequestDtoJsonTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldSerializeCorrectly() throws JsonProcessingException {
        UserRequestDto dto = UserRequestDto.builder()
                .id(1L)
                .name("John Doe")
                .email("john@example.com")
                .build();

        String json = objectMapper.writeValueAsString(dto);

        assertThat(json).contains("\"id\":1");
        assertThat(json).contains("\"name\":\"John Doe\"");
        assertThat(json).contains("\"email\":\"john@example.com\"");
    }

    @Test
    void shouldDeserializeCorrectly() throws JsonProcessingException {
        String json = "{\"id\":1,\"name\":\"John Doe\",\"email\":\"john@example.com\"}";

        UserRequestDto dto = objectMapper.readValue(json, UserRequestDto.class);

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getName()).isEqualTo("John Doe");
        assertThat(dto.getEmail()).isEqualTo("john@example.com");
    }

    @Test
    void shouldSerializeWithNullId() throws JsonProcessingException {
        UserRequestDto dto = UserRequestDto.builder()
                .name("John Doe")
                .email("john@example.com")
                .build();

        String json = objectMapper.writeValueAsString(dto);

        assertThat(json).contains("\"name\":\"John Doe\"");
        assertThat(json).contains("\"email\":\"john@example.com\"");
    }


    @Test
    void shouldDeserializeWithMissingFields() throws JsonProcessingException {
        String json = "{\"id\":1}";

        UserRequestDto dto = objectMapper.readValue(json, UserRequestDto.class);

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getName()).isNull();
        assertThat(dto.getEmail()).isNull();
    }

    @Test
    void shouldExcludeNullValuesWhenConfigured() throws JsonProcessingException {
        String json = "{\"name\":\"John\"}";

        UserRequestDto dto = objectMapper.readValue(json, UserRequestDto.class);

        assertThat(dto.getId()).isNull();
        assertThat(dto.getName()).isEqualTo("John");
        assertThat(dto.getEmail()).isNull();
    }
}
