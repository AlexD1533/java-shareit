package ru.practicum.shareit.requestItem;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RequestItemServiceImpl implements RequestItemService {
    private final RequestItemRepository requestRepository;
    private final RequestItemMapper requestItemMapper;

    public RequestItemDto create(Long userId, NewRequestItem request) {

        RequestItem newRequestItem = requestItemMapper.mapToRequestItem(userId, request);
        return requestItemMapper.mapToResponseFullDto(requestRepository.save(newRequestItem));
    }

    public List<RequestItemDto> getAllByUserId(Long userId) {
        List<RequestItem> requestList = requestRepository.findAllByUserId(userId);

        return requestItemMapper.mapToResponseFullDtoList(requestList);
    }

    public List<RequestItemDto> getAllByOwnerIdRequest(Long ownerId) {
        List<RequestItem> requestList = requestRepository.getAllByOwnerIdRequest(ownerId);
        return requestItemMapper.mapToResponseFullDtoList(requestList);
    }

    public RequestItemDto getRequestItemById(Long requestId) {
        RequestItem requestItem = requestRepository.findById(requestId).orElseThrow(() ->
        new NotFoundException("Запрос с id=" + requestId + " не найден"));

        return requestItemMapper.mapToResponseFullDto(requestItem);
    }
}
