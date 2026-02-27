package ru.practicum.shareit.item;


import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.dto.ItemDtoWithDates;
import ru.practicum.shareit.item.dto.NewItemRequest;
import ru.practicum.shareit.requestItem.NewRequestItem;
import ru.practicum.shareit.requestItem.RequestItemServiceImpl;
import ru.practicum.shareit.user.UserServiceImpl;
import ru.practicum.shareit.user.dto.NewUserRequest;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;


import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SpringBootTest
@ActiveProfiles("test")
public class ItemServiceImplTest {

    private final UserServiceImpl userService;
    private final ItemServiceImpl itemService;
    private final RequestItemServiceImpl requestItemService;

    @Test

    void getItemsByUser() {

        NewUserRequest request = new NewUserRequest("John", "john@bk.com");
        UserDto user = userService.create(request);
        Long userId = user.getId();

        NewRequestItem requestItem1 = new NewRequestItem("Trimmer");
        requestItemService.create(userId, requestItem1);


        NewItemRequest request1 = new NewItemRequest("Photo Camera", "Canon 50d", true, null );
        NewItemRequest request2 = new NewItemRequest("Guitar", "fender", true, null);

        itemService.create(userId, request1);
        itemService.create(userId, request2);

        List<ItemDtoWithDates> itemsByUser = itemService.getAllByUserId(userId);

        assertThat(itemsByUser.size(), equalTo(2));

        assertThat(itemsByUser.get(0).getOwnerId(), equalTo(userId));
        assertThat(itemsByUser.get(1).getOwnerId(), equalTo(userId));

    }


}
