package ru.practicum.shareit.item.request;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RequestItemServiceImpl implements RequestItemService {
    RequestRepository requestRepository;

    public RequestItemDto create(Long userId, NewRequestItem request) {

        RequestItem newRequestItem = RequestItemMapper.mapToRequestItem(request);

        return RequestItemMapper.mapToResponseFullDto(newRequestItem);
    }
}
