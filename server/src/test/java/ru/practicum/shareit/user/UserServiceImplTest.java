package ru.practicum.shareit.user;


import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.user.dto.NewUserRequest;
import ru.practicum.shareit.user.dto.UserDto;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;


@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SpringBootTest
@ActiveProfiles("test")
public class UserServiceImplTest {

    private final UserServiceImpl userService;


  @Test
  void testSaveUser() {

      NewUserRequest request = new NewUserRequest("John", "john@bk.com");
      UserDto user = userService.create(request);

      assertThat(user.getId(), notNullValue());
      assertThat(user.getName(), equalTo(request.getName()));
      assertThat(user.getEmail(), equalTo(request.getEmail()));

  }




}
