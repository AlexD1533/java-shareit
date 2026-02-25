package ru.practicum.shareit.item.request;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RequestItemServiceImpl implements RequestItemService {
    private final RequestItemRepository requestRepository;
    private final RequestItemMapper requestItemMapper;

    public RequestItemDto create(Long userId, NewRequestItem request) {

        RequestItem newRequestItem = requestItemMapper.mapToRequestItem(request);

        return requestItemMapper.mapToResponseFullDto(requestRepository.save(newRequestItem));
    }
}
