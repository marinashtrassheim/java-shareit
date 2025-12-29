package ru.practicum.shareit;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.*;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserMapper userMapper;
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    void create_whenEmailAlreadyExists_shouldThrowConflictException() {
        UserDto userDto = UserDto.builder().email("test@mail.com").build();
        when(userRepository.existsByEmail("test@mail.com")).thenReturn(true);

        assertThrows(ConflictException.class, () -> userService.create(userDto));
    }

    @Test
    void create_whenValidData_shouldReturnUserDto() {
        UserDto userDto = UserDto.builder().email("test@mail.com").build();
        UserEntity entity = new UserEntity();
        UserEntity savedEntity = new UserEntity();
        UserDto expectedDto = UserDto.builder().id(1L).email("test@mail.com").build();

        when(userRepository.existsByEmail("test@mail.com")).thenReturn(false);
        when(userMapper.toEntity(userDto)).thenReturn(entity);
        when(userRepository.save(entity)).thenReturn(savedEntity);
        when(userMapper.toDto(savedEntity)).thenReturn(expectedDto);

        UserDto result = userService.create(userDto);

        assertEquals(expectedDto, result);
        verify(userRepository).save(entity);
    }

    @Test
    void update_whenUserNotFound_shouldThrowNotFoundException() {
        UserDto userDto = new UserDto();
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.update(userDto, 1L));
    }

    @Test
    void update_whenEmailAlreadyUsed_shouldThrowConflictException() {
        UserDto userDto = UserDto.builder().email("new@mail.com").build();
        UserEntity entity = new UserEntity();
        entity.setEmail("old@mail.com");

        when(userRepository.findById(1L)).thenReturn(Optional.of(entity));
        when(userRepository.existsByEmail("new@mail.com")).thenReturn(true);

        assertThrows(ConflictException.class, () -> userService.update(userDto, 1L));
    }

    @Test
    void update_whenEmailNotChanged_shouldNotCheckForConflict() {
        UserDto userDto = UserDto.builder().email("same@mail.com").build();
        UserEntity entity = new UserEntity();
        entity.setEmail("same@mail.com");
        UserEntity savedEntity = new UserEntity();
        savedEntity.setId(1L);
        savedEntity.setName("Updated");
        savedEntity.setEmail("same@mail.com");

        when(userRepository.findById(1L)).thenReturn(Optional.of(entity));
        when(userRepository.save(any())).thenReturn(savedEntity);

        UserDto result = userService.update(userDto, 1L);

        assertEquals(1L, result.getId());
        assertEquals("same@mail.com", result.getEmail());
        verify(userRepository, never()).existsByEmail("same@mail.com");
    }

    @Test
    void update_whenValidData_shouldReturnUpdatedUser() {
        UserDto userDto = UserDto.builder()
                .name("Updated")
                .email("new@mail.com")
                .build();
        UserEntity entity = new UserEntity();
        entity.setName("Old");
        entity.setEmail("old@mail.com");
        UserEntity savedEntity = new UserEntity();
        savedEntity.setId(1L);
        savedEntity.setName("Updated");
        savedEntity.setEmail("new@mail.com");

        when(userRepository.findById(1L)).thenReturn(Optional.of(entity));
        when(userRepository.existsByEmail("new@mail.com")).thenReturn(false);
        when(userRepository.save(entity)).thenReturn(savedEntity);

        UserDto result = userService.update(userDto, 1L);

        assertEquals(1L, result.getId());
        assertEquals("Updated", result.getName());
        assertEquals("new@mail.com", result.getEmail());
        assertEquals("Updated", entity.getName());
        assertEquals("new@mail.com", entity.getEmail());
    }

    @Test
    void get_whenUserNotFound_shouldThrowNotFoundException() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.get(1L));
    }

    @Test
    void get_whenUserExists_shouldReturnUserDto() {
        UserEntity entity = new UserEntity();
        entity.setId(1L);
        entity.setName("Test");
        entity.setEmail("test@mail.com");

        when(userRepository.findById(1L)).thenReturn(Optional.of(entity));

        UserDto result = userService.get(1L);

        assertEquals(1L, result.getId());
        assertEquals("Test", result.getName());
        assertEquals("test@mail.com", result.getEmail());
    }

    @Test
    void delete_whenUserNotFound_shouldThrowNotFoundException() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.delete(1L));
    }

    @Test
    void delete_whenUserExists_shouldDeleteUser() {
        UserEntity entity = new UserEntity();
        when(userRepository.findById(1L)).thenReturn(Optional.of(entity));

        userService.delete(1L);

        verify(userRepository).deleteById(1L);
    }

    @Test
    void userExistsById_whenUserExists_shouldReturnTrue() {
        when(userRepository.existsById(1L)).thenReturn(true);

        assertTrue(userService.userExistsById(1L));
    }

    @Test
    void userExistsById_whenUserNotExists_shouldReturnFalse() {
        when(userRepository.existsById(1L)).thenReturn(false);

        assertFalse(userService.userExistsById(1L));
    }
}