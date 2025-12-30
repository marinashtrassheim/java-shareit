package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.UserEntity;
import ru.practicum.shareit.user.UserRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemRequestServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private ItemRequestMapper itemRequestMapper;
    @Mock
    private ItemRequestRepository itemRequestRepository;

    @InjectMocks
    private ItemRequestService itemRequestService;

    @Test
    void createRequest_whenUserNotFound_shouldThrowNotFoundException() {
        ItemRequestDto itemRequestDto = new ItemRequestDto();
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemRequestService.createRequest(itemRequestDto, 1L));
    }

    @Test
    void createRequest_whenValidData_shouldReturnItemRequestDto() {
        ItemRequestDto itemRequestDto = new ItemRequestDto();
        UserEntity user = new UserEntity();
        ItemRequestEntity entity = new ItemRequestEntity();
        ItemRequestEntity savedEntity = new ItemRequestEntity();
        ItemRequestDto expectedDto = new ItemRequestDto();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(itemRequestMapper.toEntity(itemRequestDto)).thenReturn(entity);
        when(itemRequestRepository.save(entity)).thenReturn(savedEntity);
        when(itemRequestMapper.toResponseDto(savedEntity)).thenReturn(expectedDto);

        ItemRequestDto result = itemRequestService.createRequest(itemRequestDto, 1L);

        assertEquals(expectedDto, result);
        assertEquals(user, entity.getRequester());
        verify(itemRequestRepository).save(entity);
    }

    @Test
    void getUserRequests_whenUserNotFound_shouldThrowNotFoundException() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemRequestService.getUserRequests(1L, 0, 10));
    }

    @Test
    void getUserRequests_whenUserExists_shouldReturnRequests() {
        UserEntity user = new UserEntity();
        ItemRequestEntity entity = new ItemRequestEntity();
        ItemRequestDto expectedDto = new ItemRequestDto();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(itemRequestRepository.findAllByRequester_IdOrderByCreatedDesc(eq(1L), any(Pageable.class)))
                .thenReturn(List.of(entity));
        when(itemRequestMapper.toResponseDto(entity)).thenReturn(expectedDto);

        List<ItemRequestDto> result = (List<ItemRequestDto>) itemRequestService.getUserRequests(1L, 0, 10);

        assertEquals(1, result.size());
        assertEquals(expectedDto, result.getFirst());
    }

    @Test
    void getAllRequests_shouldReturnAllRequests() {
        ItemRequestEntity entity = new ItemRequestEntity();
        ItemRequestDto expectedDto = new ItemRequestDto();

        Page<ItemRequestEntity> page = new PageImpl<>(List.of(entity));

        when(itemRequestRepository.findAll(any(Pageable.class))).thenReturn(page);
        when(itemRequestMapper.toResponseDto(entity)).thenReturn(expectedDto);

        List<ItemRequestDto> result = (List<ItemRequestDto>) itemRequestService.getAllRequests(0, 10);

        assertEquals(1, result.size());
        assertEquals(expectedDto, result.getFirst());
        verify(itemRequestRepository).findAll(any(Pageable.class));
    }

    @Test
    void getRequestById_whenRequestNotFound_shouldThrowNotFoundException() {
        when(itemRequestRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemRequestService.getRequestById(1L));
    }

    @Test
    void getRequestById_whenRequestExists_shouldReturnRequestDto() {
        ItemRequestEntity entity = new ItemRequestEntity();
        ItemRequestDto expectedDto = new ItemRequestDto();

        when(itemRequestRepository.findById(1L)).thenReturn(Optional.of(entity));
        when(itemRequestMapper.toResponseDto(entity)).thenReturn(expectedDto);

        ItemRequestDto result = itemRequestService.getRequestById(1L);

        assertEquals(expectedDto, result);
        verify(itemRequestRepository).findById(1L);
    }
}