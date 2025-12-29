package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.UserEntity;
import ru.practicum.shareit.user.UserRepository;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemRequestService {

    private final UserRepository userRepository;
    private final ItemRequestMapper itemRequestMapper;
    private final ItemRequestRepository itemRequestRepository;

    public ItemRequestDto createRequest(ItemRequestDto itemRequestDto, Long userId) {
        UserEntity requester = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        ItemRequestEntity itemRequestEntity = itemRequestMapper.toEntity(itemRequestDto);
        itemRequestEntity.setRequester(requester);
        ItemRequestEntity itemRequestEntitySaved = itemRequestRepository.save(itemRequestEntity);
        return itemRequestMapper.toResponseDto(itemRequestEntitySaved);
    }

    public Collection<ItemRequestDto> getUserRequests(Long userId, Integer from, Integer size) {
        Pageable pageable = PageRequest.of(from / size, size, Sort.by("created").descending());
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        List<ItemRequestEntity> entities = itemRequestRepository
                .findAllByRequester_IdOrderByCreatedDesc(userId, pageable);
        return entities.stream()
                .map(itemRequestMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    public Collection<ItemRequestDto> getAllRequests(Integer from, Integer size) {
        Pageable pageable = PageRequest.of(from / size, size, Sort.by("created").descending());
        return itemRequestRepository.findAll(pageable).stream()
                .map(itemRequestMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    public ItemRequestDto getRequestById(Long id) {
        ItemRequestEntity itemRequestEntity = itemRequestRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Запрос не найден"));
        return itemRequestMapper.toResponseDto(itemRequestEntity);
    }
}
