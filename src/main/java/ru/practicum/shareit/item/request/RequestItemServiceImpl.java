package ru.practicum.shareit.item.request;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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

    public List<RequestItemDto> getAll() {
        List<RequestItem> requestList = requestRepository.findAll();
        return requestItemMapper.mapToResponseFullDtoList(requestList);
    }
}
