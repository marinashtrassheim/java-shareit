package ru.practicum.shareit.user;

import ru.practicum.shareit.exception.ConflictException;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;


@Service
public class UserService {
    private final UserMapper userMapper;
    private final UserRepository userRepository;

    public UserService(UserMapper userMapper, UserRepository userRepository) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    public UserDto create(UserDto userDto) {
        if (userRepository.existsByEmail(userDto.getEmail())) {
            throw new ConflictException("Email уже используется");
        }

        UserEntity userEntity = userMapper.toEntity(userDto);
        UserEntity savedEntity = userRepository.save(userEntity);
        return userMapper.toDto(savedEntity);
    }

    public UserDto update(UserDto userDto, Long userId) {
        UserEntity entity = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        if (userDto.getEmail() != null &&
                !userDto.getEmail().equals(entity.getEmail()) &&
                userRepository.existsByEmail(userDto.getEmail())) {
            throw new ConflictException("Email уже используется");
        }

        if (userDto.getName() != null) entity.setName(userDto.getName());
        if (userDto.getEmail() != null) entity.setEmail(userDto.getEmail());

        UserEntity saved = userRepository.save(entity);

        return UserDto.builder()
                .id(saved.getId())
                .name(saved.getName())
                .email(saved.getEmail())
                .build();
    }

    public UserDto get(Long id) {
        UserEntity entity = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + id + " не найден"));

        return UserDto.builder()
                .id(entity.getId())
                .name(entity.getName())
                .email(entity.getEmail())
                .build();
    }

    public void delete(Long id) {
        userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        userRepository.deleteById(id);
    }

    public boolean userExistsById(Long userId) {
        return userRepository.existsById(userId);
    }

}
